package com.service.productorder.payloads;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class User {

	private Long userId;

	private String firstName;

	private String lastName;

	private String mobileNumber;

	private String email;

	private String password;

	private Set<Role> roles = new HashSet<>();

	private List<Address> addresses = new ArrayList<>();

}
