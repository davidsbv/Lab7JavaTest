package com.dperez.CarRegistry.repository;

import com.dperez.CarRegistry.repository.entity.UserEntity;
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
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void findByMail_ExistingUser() {

        String email = "test@test.com";
        UserEntity expectedUserEntity = UserEntity.builder().mail(email).build();

        when(userRepository.findByMail(email)).thenReturn(Optional.of(expectedUserEntity));

        Optional<UserEntity> result = userRepository.findByMail(email);

        assertTrue(result.isPresent());
        assertEquals(expectedUserEntity.getMail(), result.get().getMail());
        verify(userRepository).findByMail(email);
    }

    @Test
    void findBuMail_NonExistingUser() {

        String email = "nonExistentUser@test.com";

        when(userRepository.findByMail(email)).thenReturn(Optional.empty());

        Optional<UserEntity> result = userRepository.findByMail(email);

        assertFalse(result.isPresent());
        verify(userRepository).findByMail(email);
    }
}