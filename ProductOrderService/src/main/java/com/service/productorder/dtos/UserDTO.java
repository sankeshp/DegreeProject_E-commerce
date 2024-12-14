package com.service.productorder.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private Long userId;

	private String firstName;

	private String lastName;

	private String mobileNumber;

	private String email;

	private String password;

	private Set<RoleDTO> roleDTOS = new HashSet<>();

	private List<AddressDTO> addressDTOS = new ArrayList<>();

}
