package com.dperez.CarRegistry.repository;

import com.dperez.CarRegistry.repository.entity.BrandEntity;
import com.dperez.CarRegistry.repository.entity.CarEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarRepositoryTest {

    @Mock
    private CarRepository carRepository;

    private CarEntity testCar;

    @BeforeEach
    void setUp() {

        BrandEntity testBrand = BrandEntity.builder()
                .id(1)
                .name("Toyota")
                .build();

        testCar = CarEntity.builder()
                .id(1)
                .brand(testBrand)
                .model("Corolla")
                .mileage(57000)
                .price(19500.0)
                .year(2021)
                .description("--")
                .color("Silver")
                .fuelType("Gasolina")
                .numDoors(4)
                .build();
    }

    @Test
    void testFindById() {
        when(carRepository.findById(1)).thenReturn(Optional.of(testCar));

        Optional<CarEntity> found = carRepository.findById(1);

        assertTrue(found.isPresent());
        assertEquals(testCar, found.get());
    }

    @Test
    void testSave() {
        when(carRepository.save(any(CarEntity.class))).thenReturn(testCar);

        CarEntity saved = carRepository.save(testCar);

        assertNotNull(saved);
        assertEquals(testCar, saved);
    }

    @Test
    void testFindAll() {
        List<CarEntity> cars = Arrays.asList(testCar);
        when(carRepository.findAll()).thenReturn(cars);

        List<CarEntity> found = carRepository.findAll();

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
        assertEquals(testCar, found.get(0));
    }

    @Test
    void testDeleteById() {
        doNothing().when(carRepository).deleteById(1);

        carRepository.deleteById(1);

        verify(carRepository, times(1)).deleteById(1);
    }

}