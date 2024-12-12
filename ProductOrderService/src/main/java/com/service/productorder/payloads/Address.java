package com.service.productorder.payloads;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

	private Long addressId;

	private String street;

	private String buildingName;

	private String city;

	private String state;

	private String country;

	private String pincode;

	private List<User> users = new ArrayList<>();

}
