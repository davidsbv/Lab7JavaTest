package com.dperez.CarRegistry.service;

import com.dperez.CarRegistry.repository.entity.UserEntity;

public interface UserService {
    UserEntity save(UserEntity newUser);
}
