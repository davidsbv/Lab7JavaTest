package com.dperez.CarRegistry.controller;

import com.dperez.CarRegistry.controller.dtos.LoginRequest;
import com.dperez.CarRegistry.controller.dtos.SignUpRequest;
import com.dperez.CarRegistry.service.impl.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationServiceImpl authenticationServiceImpl;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request){
        try {
            return  ResponseEntity.ok(authenticationServiceImpl.signup(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authenticationServiceImpl.login(request));
    }


}
