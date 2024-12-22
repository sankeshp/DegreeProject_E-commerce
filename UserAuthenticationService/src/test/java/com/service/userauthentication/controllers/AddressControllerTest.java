package com.service.userauthentication.controllers;

import com.service.userauthentication.dtos.AddressDTO;
import com.service.userauthentication.entities.Address;
import com.service.userauthentication.repositories.RoleRepo;
import com.service.userauthentication.services.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private AddressController addressController;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Mock
    private RoleRepo roleRepo;

    @Test
    void testGetAddresses() {

        AddressDTO address1 = new AddressDTO(1L, "123 Main St", "New York", "NY", "10001");
        AddressDTO address2 = new AddressDTO(2L, "456 Elm St", "Los Angeles", "CA", "90001");
        List<AddressDTO> addressDTOList = Arrays.asList(address1, address2);
        when(addressService.getAddresses()).thenReturn(addressDTOList);

        ResponseEntity<List<AddressDTO>> response = addressController.getAddresses();

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(addressDTOList, response.getBody());
        verify(addressService, times(1)).getAddresses();
    }

    @Test
    void testGetAddress() {

        Long addressId = 1L;
        AddressDTO addressDTO = new AddressDTO(addressId, "123 Main St", "New York", "NY", "10001");

        when(valueOperations.get("getAddress_" + addressId)).thenReturn(null);
        when(addressService.getAddress(addressId)).thenReturn(addressDTO);

        ResponseEntity<AddressDTO> response = addressController.getAddress(addressId);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(addressDTO, response.getBody());
        verify(addressService, times(1)).getAddress(addressId);
    }

    @Test
    void testCreateAddress() {
        AddressDTO addressDTO = new AddressDTO(1, "789 Oak St", "San Francisco", "CA", "94016");
        AddressDTO savedAddressDTO = new AddressDTO(3L, "789 Oak St", "San Francisco", "CA", "94016");
        when(addressService.createAddress(addressDTO)).thenReturn(savedAddressDTO);

        ResponseEntity<AddressDTO> response = addressController.createAddress(addressDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedAddressDTO, response.getBody());
        verify(addressService, times(1)).createAddress(addressDTO);
    }

    @Test
    void testUpdateAddress() {
        Long addressId = 1L;
        Address address = new Address(addressId, "789 Oak St", "San Francisco", "CA", "94016");
        AddressDTO updatedAddressDTO = new AddressDTO(addressId, "789 Oak St", "San Francisco", "CA", "94016");
        when(addressService.updateAddress(addressId, address)).thenReturn(updatedAddressDTO);

        ResponseEntity<AddressDTO> response = addressController.updateAddress(addressId, address);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedAddressDTO, response.getBody());
        verify(addressService, times(1)).updateAddress(addressId, address);
    }

    @Test
    void testDeleteAddress() {

        Long addressId = 1L;
        String statusMessage = "Address deleted successfully";
        when(addressService.deleteAddress(addressId)).thenReturn(statusMessage);

        ResponseEntity<String> response = addressController.deleteAddress(addressId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(statusMessage, response.getBody());
        verify(addressService, times(1)).deleteAddress(addressId);
    }
}

