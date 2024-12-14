package com.service.userauthentication.controllers;

import com.service.userauthentication.entities.User;
import com.service.userauthentication.exceptions.UserNotFoundException;
import com.service.userauthentication.dtos.LoginCredentials;
import com.service.userauthentication.dtos.UserDTO;
import com.service.userauthentication.security.JWTUtil;
import com.service.userauthentication.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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

	@GetMapping("/user/email/{email}")
	public ResponseEntity<UserDTO> getUser(@PathVariable String email) {

		Object value = redisTemplate.opsForValue().get("getUser_"+email);
		if(value != null)
			return new ResponseEntity<UserDTO>((UserDTO) value, HttpStatus.FOUND);

		Optional<User> user = userService.getUserByEmail(email);

		if (user.isEmpty()) {
			return new ResponseEntity<UserDTO>(HttpStatus.NOT_FOUND);
		}

		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		redisTemplate.opsForValue().set("getUser_"+email, userDTO);
		return new ResponseEntity<UserDTO>(userDTO, HttpStatus.FOUND);
	}


	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserDTO user) throws UserNotFoundException {
		String encodedPass = passwordEncoder.encode(user.getPassword());

		user.setPassword(encodedPass);

		UserDTO userDTO = userService.registerUser(user);

		String token = jwtUtil.generateToken(userDTO.getEmail());

		return new ResponseEntity<Map<String, Object>>(Collections.singletonMap("jwt-token", token),
				HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public Map<String, Object> loginUser(@Valid @RequestBody LoginCredentials credentials) {

		UsernamePasswordAuthenticationToken authCredentials = new UsernamePasswordAuthenticationToken(
				credentials.getEmail(), credentials.getPassword());

		authenticationManager.authenticate(authCredentials);

		String token = jwtUtil.generateToken(credentials.getEmail());

		return Collections.singletonMap("jwt-token", token);
	}

}