package com.service.userauthentication.services;

import com.service.userauthentication.dtos.UserDTO;
import com.service.userauthentication.dtos.UserResponse;
import com.service.userauthentication.entities.User;

import java.util.Optional;

public interface UserService {
	UserDTO registerUser(UserDTO userDTO);
	
	UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	UserDTO getUserById(Long userId);
	
	UserDTO updateUser(Long userId, UserDTO userDTO);
	
	String deleteUser(Long userId);

	Optional<User> getUserByEmail(String email);
}
