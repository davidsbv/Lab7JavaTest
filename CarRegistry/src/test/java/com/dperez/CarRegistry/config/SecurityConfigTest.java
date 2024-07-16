package com.dperez.CarRegistry.config;

import com.dperez.CarRegistry.filter.JwtAuthenticationFilter;
import com.dperez.CarRegistry.service.impl.JwtService;
import com.dperez.CarRegistry.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

//@Profile("test")
@TestConfiguration
@EnableWebSecurity
@Import(PasswordConfig.class)
public class SecurityConfigTest {

    @MockBean
    private JwtService jwtService;

    @Autowired
    private UserServiceImpl userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userService);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/users/**", "/login", "/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/cars/**", "/brands/**").hasRole("VENDOR")
                        .requestMatchers(HttpMethod.GET, "/cars/**", "/brands/**")
                        .hasAnyRole("CLIENT", "VENDOR")
                        .requestMatchers(HttpMethod.PUT, "/cars/**", "/brands/**").hasRole("VENDOR")
                        .requestMatchers(HttpMethod.DELETE, "/cars/**", "/brands/**").hasRole("VENDOR")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(mockJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails vendor = User.builder()
                .username("vendor@vendor.com")
                .password(passwordEncoder.encode("vendorpass"))  //password("{noop}password")
                .roles("VENDOR")
                .build();

        UserDetails client = User.builder()
                .username("user@user.com")
                .password(passwordEncoder.encode("userpass"))
                .roles("CLIENT")
                .build();

        return new InMemoryUserDetailsManager(vendor, client);
    }
}