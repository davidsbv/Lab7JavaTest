package com.dperez.CarRegistry.repository;

import com.dperez.CarRegistry.repository.entity.BrandEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BrandRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    void whenFindByNameIgnoreCase_thenReturnBrand() {
        // given
        BrandEntity brand = BrandEntity.builder()
                .name("Toyota")
                .warranty(3)
                .country("Japan")
                .cars(new HashSet<>())
                .build();
        entityManager.persist(brand);
        entityManager.flush();

        // when
        Optional<BrandEntity> found = brandRepository.findByNameIgnoreCase("toyota");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Toyota");
        assertThat(found.get().getWarranty()).isEqualTo(3);
        assertThat(found.get().getCountry()).isEqualTo("Japan");
    }

    @Test
    void whenFindByNameIgnoreCase_withDifferentCase_thenReturnBrand() {
        // given
        BrandEntity brand = BrandEntity.builder()
                .name("Honda")
                .warranty(3)
                .country("Japan")
                .cars(new HashSet<>())
                .build();
        entityManager.persist(brand);
        entityManager.flush();

        // when
        Optional<BrandEntity> found = brandRepository.findByNameIgnoreCase("HONDA");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Honda");
        assertThat(found.get().getWarranty()).isEqualTo(3);
        assertThat(found.get().getCountry()).isEqualTo("Japan");
    }

    @Test
    void whenFindByNameIgnoreCase_withNonExistingName_thenReturnEmpty() {
        // given
        BrandEntity brand = BrandEntity.builder()
                .name("Ford")
                .warranty(3)
                .country("USA")
                .cars(new HashSet<>())
                .build();
        entityManager.persist(brand);
        entityManager.flush();

        // when
        Optional<BrandEntity> found = brandRepository.findByNameIgnoreCase("Chevrolet");

        // then
        assertThat(found).isEmpty();
    }
}