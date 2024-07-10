package com.dperez.CarRegistry.service.impl;

import com.dperez.CarRegistry.repository.UserRepository;
import com.dperez.CarRegistry.repository.entity.UserEntity;
import com.dperez.CarRegistry.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByMail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
            }
        };
    }

    @Override
    public UserEntity save(UserEntity newUser) {
        return userRepository.save(newUser);
    }
}
