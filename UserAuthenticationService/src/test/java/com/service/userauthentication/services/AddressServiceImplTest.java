package com.service.userauthentication.services;

import com.service.userauthentication.dtos.AddressDTO;
import com.service.userauthentication.entities.Address;
import com.service.userauthentication.entities.User;
import com.service.userauthentication.exceptions.APIException;
import com.service.userauthentication.exceptions.ResourceNotFoundException;
import com.service.userauthentication.repositories.AddressRepo;
import com.service.userauthentication.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepo addressRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAddress_ShouldReturnCreatedAddress_WhenAddressIsNew() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCountry("USA");
        addressDTO.setState("California");
        addressDTO.setCity("Los Angeles");
        addressDTO.setPincode("90001");
        addressDTO.setStreet("Main Street");
        addressDTO.setBuildingName("Building A");

        Address address = new Address();
        Address savedAddress = new Address();
        AddressDTO savedAddressDTO = new AddressDTO();

        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                addressDTO.getCountry(), addressDTO.getState(), addressDTO.getCity(), addressDTO.getPincode(), addressDTO.getStreet(), addressDTO.getBuildingName()
        )).thenReturn(null);
        when(modelMapper.map(addressDTO, Address.class)).thenReturn(address);
        when(addressRepo.save(address)).thenReturn(savedAddress);
        when(modelMapper.map(savedAddress, AddressDTO.class)).thenReturn(savedAddressDTO);

        AddressDTO result = addressService.createAddress(addressDTO);

        assertNotNull(result);
        assertEquals(savedAddressDTO, result);
        verify(addressRepo, times(1)).save(address);
    }

    @Test
    void createAddress_ShouldThrowException_WhenAddressAlreadyExists() {
        AddressDTO addressDTO = new AddressDTO();
        Address existingAddress = new Address();
        existingAddress.setAddressId(1L);

        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                addressDTO.getCountry(), addressDTO.getState(), addressDTO.getCity(), addressDTO.getPincode(), addressDTO.getStreet(), addressDTO.getBuildingName()
        )).thenReturn(existingAddress);

        APIException exception = assertThrows(APIException.class, () -> addressService.createAddress(addressDTO));

        assertEquals("Address already exists with addressId: 1", exception.getMessage());
        verify(addressRepo, never()).save(any(Address.class));
    }

    @Test
    void getAddresses_ShouldReturnListOfAddresses() {
        Address address1 = new Address();
        Address address2 = new Address();
        AddressDTO addressDTO1 = new AddressDTO();
        AddressDTO addressDTO2 = new AddressDTO();

        when(addressRepo.findAll()).thenReturn(Arrays.asList(address1, address2));
        when(modelMapper.map(address1, AddressDTO.class)).thenReturn(addressDTO1);
        when(modelMapper.map(address2, AddressDTO.class)).thenReturn(addressDTO2);

        List<AddressDTO> result = addressService.getAddresses();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(addressDTO1));
        assertTrue(result.contains(addressDTO2));
    }

    @Test
    void getAddress_ShouldReturnAddress_WhenAddressExists() {
        Long addressId = 1L;
        Address address = new Address();
        AddressDTO addressDTO = new AddressDTO();

        when(addressRepo.findById(addressId)).thenReturn(Optional.of(address));
        when(modelMapper.map(address, AddressDTO.class)).thenReturn(addressDTO);

        AddressDTO result = addressService.getAddress(addressId);

        assertNotNull(result);
        assertEquals(addressDTO, result);
    }

    @Test
    void getAddress_ShouldThrowException_WhenAddressDoesNotExist() {
        Long addressId = 1L;

        when(addressRepo.findById(addressId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> addressService.getAddress(addressId));

        assertEquals("Address not found with addressId: 1", exception.getMessage());
    }

    @Test
    void deleteAddress_ShouldThrowException_WhenAddressDoesNotExist() {
        Long addressId = 1L;

        when(addressRepo.findById(addressId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> addressService.deleteAddress(addressId));

        assertEquals("Address not found with addressId: 1", exception.getMessage());
    }
}
