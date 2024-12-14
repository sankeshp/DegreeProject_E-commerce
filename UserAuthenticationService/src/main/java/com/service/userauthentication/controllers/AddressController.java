package com.service.userauthentication.controllers;

import com.service.userauthentication.entities.Address;
import com.service.userauthentication.dtos.AddressDTO;
import com.service.userauthentication.services.AddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "E-Commerce Application")
public class AddressController {
	
	@Autowired
	private AddressService addressService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@GetMapping("/addresses")
	public ResponseEntity<List<AddressDTO>> getAddresses() {
		List<AddressDTO> addressDTOs = addressService.getAddresses();
		return new ResponseEntity<List<AddressDTO>>(addressDTOs, HttpStatus.FOUND);
	}

	@GetMapping("/addresses/{addressId}")
	public ResponseEntity<AddressDTO> getAddress(@PathVariable Long addressId) {

		Object value = redisTemplate.opsForValue().get("getAddress_"+addressId);
		if(value != null)
			return new ResponseEntity<AddressDTO>((AddressDTO) value, HttpStatus.FOUND);

		AddressDTO addressDTO = addressService.getAddress(addressId);

		redisTemplate.opsForValue().set("getAddress_"+addressId, addressDTO);
		return new ResponseEntity<AddressDTO>(addressDTO, HttpStatus.FOUND);
	}

	@PostMapping("/address")
	public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
		AddressDTO savedAddressDTO = addressService.createAddress(addressDTO);
		
		return new ResponseEntity<AddressDTO>(savedAddressDTO, HttpStatus.CREATED);
	}

	@PutMapping("/addresses/{addressId}")
	public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId, @RequestBody Address address) {
		AddressDTO addressDTO = addressService.updateAddress(addressId, address);
		
		return new ResponseEntity<AddressDTO>(addressDTO, HttpStatus.OK);
	}
	
	@DeleteMapping("/addresses/{addressId}")
	public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
		String status = addressService.deleteAddress(addressId);
		
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
}
