package com.service.userauthentication.services;

import com.service.userauthentication.configs.AppConstants;
import com.service.userauthentication.dtos.*;
import com.service.userauthentication.entities.*;
import com.service.userauthentication.exceptions.APIException;
import com.service.userauthentication.exceptions.ResourceNotFoundException;
import com.service.userauthentication.repositories.AddressRepo;
import com.service.userauthentication.repositories.RoleRepo;
import com.service.userauthentication.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private AddressRepo addressRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    private User user;
    private UserDTO userDTO;
    private Role role;
    private Address address;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John", "Doe", "john.doe@example.com", "password", "1234567890", new HashSet<>(), new ArrayList<>());
        userDTO = new UserDTO("John", "Doe", "john.doe@example.com", "password", "1234567890", new AddressDTO());
        role = new Role(AppConstants.USER_ID, "USER");
        address = new Address("USA", "California", "Los Angeles", "90001", "Main Street", "Building A");
    }

    @Test
    void getAllUsers_ShouldReturnUserResponse_WhenUsersExist() {
        List<User> users = List.of(user);
        Page<User> userPage = mock(Page.class);
        when(userPage.getContent()).thenReturn(users);
        when(userPage.getNumber()).thenReturn(0);
        when(userPage.getSize()).thenReturn(10);
        when(userPage.getTotalElements()).thenReturn(1L);
        when(userPage.getTotalPages()).thenReturn(1);
        when(userPage.isLast()).thenReturn(true);
        when(userRepo.findAll(any(Pageable.class))).thenReturn(userPage);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserResponse response = userService.getAllUsers(0, 10, "id", "asc");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getUserById_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void deleteUser_ShouldReturnSuccessMessage_WhenUserExists() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        String result = userService.deleteUser(1L);

        assertEquals("User with userId 1 deleted successfully!!!", result);
        verify(userRepo, times(1)).delete(user);
    }

    @Test
    void deleteUser_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenUserExists() {
        when(userRepo.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail("john.doe@example.com");

        assertTrue(result.isPresent());
        assertEquals(user.getEmail(), result.get().getEmail());
    }

    @Test
    void getUserByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        when(userRepo.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail("john.doe@example.com");

        assertTrue(result.isEmpty());
    }
}
