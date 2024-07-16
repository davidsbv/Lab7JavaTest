package com.dperez.CarRegistry.controller;

import com.dperez.CarRegistry.config.SecurityConfigTest;
import com.dperez.CarRegistry.controller.dtos.LoginRequest;
import com.dperez.CarRegistry.controller.dtos.LoginResponse;
import com.dperez.CarRegistry.controller.dtos.SignUpRequest;
import com.dperez.CarRegistry.filter.JwtAuthenticationFilter;
import com.dperez.CarRegistry.service.impl.AuthenticationServiceImpl;
import com.dperez.CarRegistry.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@Import(SecurityConfigTest.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationServiceImpl authenticationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void signup_Success() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("John Doe", "john@test.com", "password123");
        LoginResponse signUpResponse = new LoginResponse();
        signUpResponse.setJwt("Jwt123");

        when(authenticationService.signup(any(SignUpRequest.class))).thenReturn(signUpResponse);

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("Jwt123"));

        verify(authenticationService, times(1)).signup(any(SignUpRequest.class));
    }

    @Test
    void signup_Failure() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("John Doe", "john@example.com", "password123");

        when(authenticationService.signup(any(SignUpRequest.class))).thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));

        verify(authenticationService, times(1)).signup(any(SignUpRequest.class));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john@test.com", "password123");
        LoginResponse loginResponse = new LoginResponse("token123");

        when(authenticationService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("token123"));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_Failure() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john@test.com", "wrongpassword");

        when(authenticationService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid User or Password"));

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Invalid User or Password"));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }
}