package com.dperez.CarRegistry.repository;


import com.dperez.CarRegistry.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByMail(String mail);
}
