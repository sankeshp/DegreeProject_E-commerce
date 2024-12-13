package com.service.user.services;

import com.service.user.dtos.UserDTO;
import com.service.user.dtos.UserResponse;
import com.service.user.entities.User;

import java.util.Optional;

public interface UserService {
	UserDTO registerUser(UserDTO userDTO);
	
	UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	UserDTO getUserById(Long userId);
	
	UserDTO updateUser(Long userId, UserDTO userDTO);
	
	String deleteUser(Long userId);

	Optional<User> getUserByEmail(String email);
}
