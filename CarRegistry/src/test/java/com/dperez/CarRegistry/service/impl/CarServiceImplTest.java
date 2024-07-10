package com.dperez.CarRegistry.service.impl;

import com.dperez.CarRegistry.repository.BrandRepository;
import com.dperez.CarRegistry.repository.CarRepository;
import com.dperez.CarRegistry.repository.entity.BrandEntity;
import com.dperez.CarRegistry.repository.entity.CarEntity;
import com.dperez.CarRegistry.repository.mapper.BrandEntityMapper;
import com.dperez.CarRegistry.repository.mapper.CarEntityMapper;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CarEntityMapper carEntityMapper;

    @Mock
    private BrandEntityMapper brandEntityMapper;

    @InjectMocks
    private CarServiceImpl carService;

    private Car car1, car2;
    private Brand toyotaBrand, hondaBrand;
    private CarEntity carEntity1, carEntity2;
    private BrandEntity toyotaBrandEntity, hondaBrandEntity;

    @BeforeEach
    void setUp(){

        toyotaBrandEntity = new BrandEntity();
        toyotaBrandEntity.setName("Toyota");

        toyotaBrand = new Brand();
        toyotaBrand.setName("Toyota");

        car1 = new Car();
        car1.setId(1);
        car1.setBrand(toyotaBrand);

        carEntity1 = new CarEntity();
        carEntity1.setId(1);
        carEntity1.setBrand(toyotaBrandEntity);

        hondaBrandEntity = new BrandEntity();
        hondaBrandEntity.setName("Honda");

        hondaBrand = new Brand();
        hondaBrand.setName("Honda");

        car2 = new Car();
        car2.setId(2);
        car2.setBrand(hondaBrand);

        carEntity2 = new CarEntity();
        carEntity2.setId(2);
        carEntity2.setBrand(hondaBrandEntity);

    }

    @Test
    void addCarSuccess() {
        // Simular existencia de marca
        when(brandRepository.findByNameIgnoreCase("Toyota")).thenReturn(Optional.of(toyotaBrandEntity));

        // Simular que la id no existe aún
        when(carRepository.existsById(1)).thenReturn(false);

        // Simular convesiones entre enitites y models
        when(brandEntityMapper.brandEntityToBrand(toyotaBrandEntity)).thenReturn(toyotaBrand);
        when(carEntityMapper.carToCarEntity(car1)).thenReturn(carEntity1);

        // Simular guardado en base de datos
        when(carRepository.save(carEntity1)).thenReturn(carEntity1);

        // Simular conversión entre car entity y car después de guardar
        when(carEntityMapper.carEntityToCar(carEntity1)).thenReturn(car1);

        // Llamar al método testeado
        Car newAddedCar = carService.addCar(car1);

        // Verificar búsqueda de la marca
        verify(brandRepository, times(1)).findByNameIgnoreCase("Toyota");

        // Veirificar comprobación de inexistencia del coche
        verify(carRepository,times(1)).existsById(1);

        // Verificar que se accedión al repositorio
        verify(carRepository, times(1)).save(carEntity1);

        // Verificar que las conversiones se hicieron correctamente
        verify(brandEntityMapper, times(1)).brandEntityToBrand(toyotaBrandEntity);
        verify(carEntityMapper, times(1)).carToCarEntity(car1);
        verify(carEntityMapper, times(1)).carEntityToCar(carEntity1);

        // Assert coche añadido correctamente
        assertEquals(car1, newAddedCar);

    }

    @Test
    void addCarWhenIdAlreadyExists() {
        // Simulación de coche con ID existente
        when(carRepository.existsById(1)).thenReturn(true);

        // Verificar que se lance la excepción correspondiente
        assertThrows(IllegalArgumentException.class, () -> carService.addCar(car1));

    }

    @Test
    void addCarWhenBrandNotFound() {
        // Simulación de marca que no existe
        toyotaBrand.setName("NonExistingBrand");
        when(brandRepository.findByNameIgnoreCase("NonExistingBrand")).thenReturn(Optional.empty());

        // Verificar que se lance la excepción correspondiente
        assertThrows(IllegalArgumentException.class, () -> carService.addCar(car1));
    }


    @Test
    void addBunchCarsSuccess() throws Exception {
        // Simulación de una lista de coches válidos
        List<Car> cars = Arrays.asList(car1, car2);

        when(brandRepository.findByNameIgnoreCase("Toyota")).thenReturn(Optional.of(toyotaBrandEntity));
        when(brandRepository.findByNameIgnoreCase("Honda")).thenReturn(Optional.of(hondaBrandEntity));

        // Simulación de la conversión de Car a CarEntity
        when(carEntityMapper.carToCarEntity(car1)).thenReturn(carEntity1);
        when(carEntityMapper.carToCarEntity(car2)).thenReturn(carEntity2);

        // Simulación de guardar los coches
        when(carRepository.save(carEntity1)).thenReturn(carEntity1);
        when(carRepository.save(carEntity2)).thenReturn(carEntity2);

        // Simulación de la conversión de CarEntity a Car
        when(carEntityMapper.carEntityToCar(carEntity1)).thenReturn(car1);
        when(carEntityMapper.carEntityToCar(carEntity2)).thenReturn(car2);

        // Llamada al método de servicio
        CompletableFuture<List<Car>> resultFuture = carService.addBunchCars(cars);

        // Verificación del resultado
        assertDoesNotThrow(() -> resultFuture.get()); // Verifica que no haya excepciones al obtener el resultado

        // Verificación de la interacción con el repositorio de marcas
        verify(brandRepository, times(1)).findByNameIgnoreCase("Toyota");
        verify(brandRepository, times(1)).findByNameIgnoreCase("Honda");


        // Verificación de la interacción con el repositorio de coches
        verify(carRepository, times(1)).save(carEntity1);
        verify(carRepository, times(1)).save(carEntity2);

        // Verificación del contenido del resultado
        List<Car> result = resultFuture.get();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(car1.getId(), result.get(0).getId());
        assertEquals(car2.getId(), result.get(1).getId());
    }

    @Test
    void getCarById() {
    }

    @Test
    void updateCarById() {
    }

    @Test
    void updateBunchCars() {
    }

    @Test
    void deleteCarById() {
    }

    @Test
    void getAllCars() {
    }
}