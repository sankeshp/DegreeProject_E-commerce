package com.service.userauthentication.controllers;

import com.service.userauthentication.dtos.SendNotificationMessageDTO;
import com.service.userauthentication.entities.User;
import com.service.userauthentication.exceptions.UserNotFoundException;
import com.service.userauthentication.dtos.LoginCredentials;
import com.service.userauthentication.dtos.UserDTO;
import com.service.userauthentication.security.JWTUtil;
import com.service.userauthentication.services.UserService;
import com.service.userauthentication.configs.KafkaNotificationProducerClient;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	public KafkaNotificationProducerClient kafkaNotificationProducerClient;

	private static final Logger logger = LogManager.getLogger(AuthController.class);

	@GetMapping("/user/email/{email}")
	public ResponseEntity<UserDTO> getUser(@PathVariable String email) {
        logger.info("Getting user by email: {}", email);
		Object value = redisTemplate.opsForValue().get("getUser_"+email);
		if(value != null)
			return new ResponseEntity<UserDTO>((UserDTO) value, HttpStatus.FOUND);

		Optional<User> user = userService.getUserByEmail(email);

		if (user.isEmpty()) {
			return new ResponseEntity<UserDTO>(HttpStatus.NOT_FOUND);
		}

		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		redisTemplate.opsForValue().set("getUser_"+email, userDTO);
        logger.info("User fetched: {}", userDTO);
		return new ResponseEntity<UserDTO>(userDTO, HttpStatus.FOUND);
	}


	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserDTO user) throws UserNotFoundException {

        logger.info("Registering user: {}", user);

		String encodedPass = passwordEncoder.encode(user.getPassword());

		user.setPassword(encodedPass);

		UserDTO userDTO = userService.registerUser(user);

		SendNotificationMessageDTO sendNotificationMessageDTO = new SendNotificationMessageDTO();
		sendNotificationMessageDTO.setTo(userDTO.getEmail());
		sendNotificationMessageDTO.setSubject("Welcome");
		sendNotificationMessageDTO.setBody("User has been created for "+ userDTO.getFirstName() + " " + userDTO.getLastName() );
		kafkaNotificationProducerClient.publishNotiificationEvent(sendNotificationMessageDTO);

        logger.info("User created: {}", userDTO);
		return new ResponseEntity<>(Collections.singletonMap("user_details", userDTO),
				HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public Map<String, Object> loginUser(@Valid @RequestBody LoginCredentials credentials) {
        logger.info("Login credentials: {}", credentials);
		UsernamePasswordAuthenticationToken authCredentials = new UsernamePasswordAuthenticationToken(
				credentials.getEmail(), credentials.getPassword());

		authenticationManager.authenticate(authCredentials);

		String token = jwtUtil.generateToken(credentials.getEmail());
        logger.info("Login token: {}", token);
		return Collections.singletonMap("jwt-token", token);
	}

}