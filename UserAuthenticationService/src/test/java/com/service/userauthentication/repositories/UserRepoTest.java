package com.service.userauthentication.repositories;

import com.service.userauthentication.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepoTest {

    @Mock
    private UserRepo userRepo;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void findByAddress_ShouldReturnUsers_WhenAddressIdIsValid() {
        List<User> users = List.of(user);
        when(userRepo.findByAddress(1L)).thenReturn(users);

        List<User> result = userRepo.findByAddress(1L);

        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
        verify(userRepo, times(1)).findByAddress(1L);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailIsValid() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userRepo.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepo, times(1)).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailIsInvalid() {
        when(userRepo.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userRepo.findByEmail("invalid@example.com");

        assertTrue(result.isEmpty());
        verify(userRepo, times(1)).findByEmail("invalid@example.com");
    }
}
