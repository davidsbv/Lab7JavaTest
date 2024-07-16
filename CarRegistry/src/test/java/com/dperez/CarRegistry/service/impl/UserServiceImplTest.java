package com.dperez.CarRegistry.service.impl;

import com.dperez.CarRegistry.repository.UserRepository;
import com.dperez.CarRegistry.repository.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {

        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void userDetailsService_LoadUserByUsername_Success() {

        String email = "test@test.com";
        UserEntity mockUser = new UserEntity();
        mockUser.setMail(email);
        when(userRepository.findByMail(email)).thenReturn(Optional.of(mockUser));

        UserDetailsService userDetailsService = userService.userDetailsService();

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());

        verify(userRepository, times(1)).findByMail(email);
    }

    @Test
    void userDetailsService_LoadUserByUsername_NotFound() {

        String email = "nonExistentUser@test.com";
        when(userRepository.findByMail(email)).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = userService.userDetailsService();

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        verify(userRepository, times(1)).findByMail(email);

    }

    @Test
    void save_NewUser_Success() {

        String email = "newUserMail@test.com";

        UserEntity newUser = UserEntity.builder().mail(email).build();
        when(userRepository.save(any(UserEntity.class))).thenReturn(newUser);

        UserEntity savedUser = userRepository.save(newUser);

        assertNotNull(savedUser);
        assertEquals(newUser.getMail(), savedUser.getMail());

        verify(userRepository, times(1)).save(newUser);
    }
}