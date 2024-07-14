package com.dperez.CarRegistry.repository;

import com.dperez.CarRegistry.repository.entity.RoleEntity;
import com.dperez.CarRegistry.repository.entity.RoleName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Mock
    private RoleRepository roleRepository;

    @Test
    void testFindByName_ExistingRole() {
        RoleName roleName = RoleName.ROLE_CLIENT;
        RoleEntity expectedRole = new RoleEntity();
        expectedRole.setName(roleName);

        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(expectedRole));

        Optional<RoleEntity> result = roleRepository.findByName(roleName);

        assertTrue(result.isPresent());
        assertEquals(roleName, result.get().getName());
        verify(roleRepository).findByName(roleName);
    }

    @Test
    void testFindByName_NonExistingRole() {
        RoleName roleName = RoleName.ROLE_ADMIN;

        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        Optional<RoleEntity> result = roleRepository.findByName(roleName);

        assertFalse(result.isPresent());
        verify(roleRepository).findByName(roleName);
    }
}