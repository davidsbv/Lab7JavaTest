package com.dperez.CarRegistry.service;

import com.dperez.CarRegistry.controller.dtos.LoginRequest;
import com.dperez.CarRegistry.controller.dtos.LoginResponse;
import com.dperez.CarRegistry.controller.dtos.SignUpRequest;
import org.apache.coyote.BadRequestException;

public interface AuthenticationService {

    public LoginResponse signup(SignUpRequest request) throws BadRequestException;

    public LoginResponse login(LoginRequest request);
}
