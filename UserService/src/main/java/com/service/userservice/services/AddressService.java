package com.service.userservice.services;

import com.service.userservice.entities.Address;
import com.service.userservice.dtos.AddressDTO;

import java.util.List;

public interface AddressService {
	
	AddressDTO createAddress(AddressDTO addressDTO);
	
	List<AddressDTO> getAddresses();
	
	AddressDTO getAddress(Long addressId);
	
	AddressDTO updateAddress(Long addressId, Address address);
	
	String deleteAddress(Long addressId);
}
