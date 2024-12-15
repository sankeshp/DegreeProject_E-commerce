package com.service.userauthentication.controllers;

import com.service.userauthentication.configs.AppConstants;
import com.service.userauthentication.dtos.UserDTO;
import com.service.userauthentication.dtos.UserResponse;
import com.service.userauthentication.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class UserController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final Logger logger = LogManager.getLogger(UserController.class);
	
	@GetMapping("/admin/users")
	public ResponseEntity<UserResponse> getUsers(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_USERS_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
		logger.info("Getting All Users");
		UserResponse userResponse = userService.getAllUsers(pageNumber, pageSize, sortBy, sortOrder);
		logger.info("Returning UserResponse");
		return new ResponseEntity<UserResponse>(userResponse, HttpStatus.FOUND);
	}
	
	@GetMapping("/public/users/{userId}")
	public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        logger.info("Getting User with ID: {}", userId);
		Object value = redisTemplate.opsForValue().get("getUserById_"+userId);
		if(value != null)
			return new ResponseEntity<UserDTO>((UserDTO) value, HttpStatus.FOUND);

		UserDTO user = userService.getUserById(userId);
		redisTemplate.opsForValue().set("getUserById_"+userId, user);
        logger.info("Returning UserDTO{}", user);
		return new ResponseEntity<UserDTO>(user, HttpStatus.FOUND);
	}
	
	@PutMapping("/public/users/{userId}")
	public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
        logger.info("Updating User with ID: {}", userId);
		UserDTO updatedUser = userService.updateUser(userId, userDTO);
		logger.info("Returning UserDTO");
		return new ResponseEntity<UserDTO>(updatedUser, HttpStatus.OK);
	}
	
	@DeleteMapping("/admin/users/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        logger.info("Deleting User with ID: {}", userId);
		String status = userService.deleteUser(userId);
        logger.info("Returning status: {}", status);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
}
