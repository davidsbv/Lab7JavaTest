package com.dperez.CarRegistry.service.impl;

import com.dperez.CarRegistry.controller.dtos.LoginRequest;
import com.dperez.CarRegistry.controller.dtos.LoginResponse;
import com.dperez.CarRegistry.controller.dtos.SignUpRequest;
import com.dperez.CarRegistry.repository.RoleRepository;
import com.dperez.CarRegistry.repository.UserRepository;
import com.dperez.CarRegistry.repository.entity.RoleEntity;
import com.dperez.CarRegistry.repository.entity.RoleName;
import com.dperez.CarRegistry.repository.entity.UserEntity;
import com.dperez.CarRegistry.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final RoleName DEFAULT_ROLE = RoleName.ROLE_CLIENT; // Rol por defecto

    @Override
    public LoginResponse signup(SignUpRequest request) throws BadRequestException {
        // Buscar rol por defecto en la base de datos
        RoleEntity defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new BadRequestException("Default role not found"));

        // Construir el objeto UserEntity con el rol asignado
        var user = UserEntity
                .builder()
                .name(request.getName())
                .mail(request.getMail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(defaultRole)
                .build();

        // Guardar el usuario en la base de datos
        user = userService.save(user);

        // Generar el token JWT para el usuario registrado
        var jwt = jwtService.generateToken(user);

        // Devolver la respuesta de inicio de sesión con el token JWT
        return LoginResponse.builder().jwt(jwt).build();
    }

    @Override
    public LoginResponse login(LoginRequest request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUser(), request.getPassword()));
        var user = userRepository.findByMail(request.getUser())
                .orElseThrow(() -> new IllegalArgumentException("Invalid User or Password"));

        var jwt = jwtService.generateToken(user);
        return LoginResponse.builder().jwt(jwt).build();
    }
}
