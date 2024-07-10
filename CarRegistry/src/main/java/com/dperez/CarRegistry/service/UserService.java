package com.dperez.CarRegistry.service;

import com.dperez.CarRegistry.repository.entity.UserEntity;

public interface UserService {
    public UserEntity save(UserEntity newUser);
}
