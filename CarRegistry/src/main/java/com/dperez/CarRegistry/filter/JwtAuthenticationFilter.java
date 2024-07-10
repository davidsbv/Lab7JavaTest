package com.dperez.CarRegistry.filter;

import com.dperez.CarRegistry.service.UserService;
import com.dperez.CarRegistry.service.impl.JwtService;
import com.dperez.CarRegistry.service.impl.UserServiceImpl;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserServiceImpl userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;



        if (StringUtils.isEmpty(authHeader)) {
            log.error("Header error");
            log.error("authHeader -> {}",authHeader);
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Bearer xxxx
        log.info("JWT -> {}", jwt);

        userEmail = jwtService.extractUserName(jwt);

        // Verificar si el nombre de usuario no es nulo y si el contexto de seguridad no tiene autenticación configurada
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);

            // Verificar si el token JWT es válido
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Crear un objeto UsernamePasswordAuthenticationToken con los detalles del usuario
                // y configurarlo en el contexto de seguridad
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("User authenticated: " + userDetails.getUsername());
            } else {
                log.warn("Invalid JWT token");
            }
        }
        // Continuar con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }

}
