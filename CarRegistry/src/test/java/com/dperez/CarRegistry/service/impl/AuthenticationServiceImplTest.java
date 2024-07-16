package com.dperez.CarRegistry.service.impl;

import com.dperez.CarRegistry.controller.dtos.LoginRequest;
import com.dperez.CarRegistry.controller.dtos.LoginResponse;
import com.dperez.CarRegistry.controller.dtos.SignUpRequest;
import com.dperez.CarRegistry.repository.RoleRepository;
import com.dperez.CarRegistry.repository.UserRepository;
import com.dperez.CarRegistry.repository.entity.RoleEntity;
import com.dperez.CarRegistry.repository.entity.RoleName;
import com.dperez.CarRegistry.repository.entity.UserEntity;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void testSignup_Success() throws BadRequestException {

        SignUpRequest request = new SignUpRequest("John Doe", "john@test.com", "password123");
        RoleEntity defaultRole = RoleEntity.builder().name(RoleName.ROLE_CLIENT).build();

        UserEntity savedUser = UserEntity.builder().name("John Doe")
                .mail("john@test.com").role(defaultRole).build();

        when(roleRepository.findByName(RoleName.ROLE_CLIENT)).thenReturn(Optional.of(defaultRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.save(any(UserEntity.class))).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser)).thenReturn("jwt_token");

        LoginResponse response = authenticationService.signup(request);

        assertNotNull(response);
        assertEquals("jwt_token", response.getJwt());
        verify(userService).save(any(UserEntity.class));
        verify(jwtService).generateToken(savedUser);
    }

    @Test
    void testSignup_DefaultRoleNotFound() {

        SignUpRequest request = new SignUpRequest("John Doe", "john@test.com", "password123");
        when(roleRepository.findByName(RoleName.ROLE_CLIENT)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authenticationService.signup(request));
    }

    @Test
    void testLogin_Success() {

        LoginRequest request = new LoginRequest("john@test.com", "password123");
        UserEntity user = UserEntity.builder().mail("john@test.com").build();

        when(userRepository.findByMail("john@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt_token");

        LoginResponse response = authenticationService.login(request);

        assertNotNull(response);
        assertEquals("jwt_token", response.getJwt());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(user);
    }

    @Test
    void testLogin_InvalidUser() {

        LoginRequest request = new LoginRequest("john@test.com", "password123");
        when(userRepository.findByMail("john@test.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authenticationService.login(request));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}