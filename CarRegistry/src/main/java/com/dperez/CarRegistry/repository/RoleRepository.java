package com.dperez.CarRegistry.repository;

import com.dperez.CarRegistry.repository.entity.RoleEntity;
import com.dperez.CarRegistry.repository.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    Optional<RoleEntity> findByName(RoleName name);
}
