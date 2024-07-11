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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(CarServiceImplTest.class);
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

        // Simular no existencia de la Id
        when(carRepository.existsById(1)).thenReturn(false);
        when(carRepository.existsById(2)).thenReturn(false);

        // Simular la existencia de la Marca
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
    void addBunchCarsWhenIdAlreadyExists() {

        List<Car> cars = Arrays.asList(car1);

        // Simula que el coche con ID 1 ya existe
        when(carRepository.existsById(1)).thenReturn(true);

        try {
            // Captura la excepción y verifica su tipo y mensaje

            CompletableFuture<List<Car>> resultFuture = carService.addBunchCars(cars);
            assertThrows(ExecutionException.class, resultFuture::get);

        } catch (IllegalArgumentException e) {

            assertEquals("The Id 1 already exists", e.getMessage());

            // Verifica que el método existsById fue llamado una vez
            verify(carRepository, times(1)).existsById(1);

            // Verifica que no se llamaron los métodos siguientes
            verify(brandRepository, never()).findByNameIgnoreCase(anyString());
            verify(carEntityMapper, never()).carToCarEntity(any(Car.class));
            verify(carRepository, never()).save(any(CarEntity.class));

        }
    }

    @Test
    void addBunchCarsWhenBrandDoesNotExists(){

        String noneExistentBrand = "NonexistentBrand";
        toyotaBrand.setName(noneExistentBrand);
        car1.setBrand(toyotaBrand);

        List<Car> cars = Arrays.asList(car1);

        when(brandRepository.findByNameIgnoreCase(noneExistentBrand)).thenReturn(Optional.empty());

        try {
            CompletableFuture<List<Car>> resultFuture = carService.addBunchCars(cars);

            assertThrows(IllegalArgumentException.class, resultFuture::get);
        } catch (IllegalArgumentException e) {

            verify(brandRepository, times(1)).findByNameIgnoreCase(noneExistentBrand);
            verify(carEntityMapper, never()).carToCarEntity(any(Car.class));
            verify(carRepository, never()).save(any(CarEntity.class));

            assertEquals("Brand NonexistentBrand does not exist", e.getMessage());
        }
    }

    @Test
    void getCarByIdSuccess() {

        when(carRepository.findById(1)).thenReturn(Optional.of(carEntity1));

        when(carEntityMapper.carEntityToCar(carEntity1)).thenReturn(car1);
        Car carFound = carService.getCarById(1);
        assertEquals(car1,carFound);

        verify(carRepository, times(1)).findById(1);
        verify(carEntityMapper, times(1)).carEntityToCar(carEntity1);
    }

    @Test
    void getCarWhenByIdNotFound() {

        when(carRepository.findById(77)).thenReturn(Optional.empty());

        Car carFound = carService.getCarById(77);

        assertNull(carFound);

        verify(carRepository, times(1)).findById(77);
        verify(carEntityMapper, never()).carEntityToCar(any());

    }

    @Test
    void updateCarByIdSuccess() {

        Integer id = 1;
        Car newDataCar = new Car();
        newDataCar.setBrand(toyotaBrand);
        newDataCar.setId(id);

        carEntity1.setId(id);
        carEntity1.setBrand(toyotaBrandEntity);


        when(carRepository.existsById(id)).thenReturn(true);
        when(brandRepository.findByNameIgnoreCase(newDataCar.getBrand().getName())).
                thenReturn(Optional.of(toyotaBrandEntity));
        when(carEntityMapper.carToCarEntity(newDataCar)).thenReturn(carEntity1);
        when(carRepository.save(carEntity1)).thenReturn(carEntity1);
        when(carEntityMapper.carEntityToCar(carEntity1)).thenReturn(newDataCar);

        Car result = carService.updateCarById(id, newDataCar);

        assertEquals(newDataCar, result);

        verify(carRepository, times(1)).save(carEntity1);
        verify(carEntityMapper, times(1)).carToCarEntity(newDataCar);
        verify(carEntityMapper, times(1)).carEntityToCar(carEntity1);

    }

    @Test
    void updateCarByIdWhenBrandNotFound(){
        Integer id = 1;
        car1.getBrand().setName("NonExistengBrand");

        when(brandRepository.findByNameIgnoreCase("NonExistengBrand")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> carService.updateCarById(id,car1));

        verify(carRepository, never()).existsById(anyInt());
        verifyNoMoreInteractions(brandRepository, carEntityMapper);

    }

    @Test
    void updateCarByIdWhenCarIdNotFound(){

        Integer id = 77;

        when(brandRepository.findByNameIgnoreCase(car1.getBrand().getName())).
                thenReturn(Optional.of(toyotaBrandEntity));
        when(carRepository.existsById(id)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> carService.updateCarById(id, car1));

        verifyNoMoreInteractions(carRepository, brandRepository, carEntityMapper);
    }

    @Test
    void updateCarByIdWhenCarIdIsNull(){

        assertThrows(IllegalArgumentException.class, () -> carService.updateCarById(null, car1));

        verifyNoInteractions(carRepository, carEntityMapper);
    }

    @Test
    void updateBunchCarsSuccess() throws ExecutionException, InterruptedException {
       List<Car> cars = Arrays.asList(car1, car2);

        // Mock BrandEntityMapper
        when(brandEntityMapper.brandToBrandEntity(any(Brand.class))).thenReturn(toyotaBrandEntity);

        // Mock BrandRepository
        when(brandRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(toyotaBrandEntity));

        // Mock CarRepository

        when(carRepository.existsById(anyInt())).thenReturn(true);
        when(carRepository.save(any(CarEntity.class))).thenReturn(carEntity1, carEntity2);

        // Mock CarEntityMapper
        when(carEntityMapper.carToCarEntity(any(Car.class))).thenReturn(carEntity1, carEntity2);
        when(carEntityMapper.carEntityToCar(any(CarEntity.class))).thenReturn(car1, car2);


        CompletableFuture<List<Car>> result = carService.updateBunchCars(cars);

        List<Car> updatedCars = result.get();
        assertEquals(2, updatedCars.size());
        assertEquals(1, updatedCars.get(0).getId());
        assertEquals(2, updatedCars.get(1).getId());

        verify(brandRepository, times(2)).findByNameIgnoreCase(anyString());
        verify(carRepository, times(2)).existsById(anyInt());
        verify(carRepository, times(2)).save(any(CarEntity.class));
        verify(carEntityMapper, times(2)).carToCarEntity(any(Car.class));
        verify(carEntityMapper, times(2)).carEntityToCar(any(CarEntity.class));
    }

    @Test
    void updateBunchCarsWhenBrandNotFound(){
        Brand nonExistentBrand = new Brand();
        nonExistentBrand.setName("NonExistentBrand");
        car1.setBrand(nonExistentBrand);
        List<Car> cars = Arrays.asList(car1);

        BrandEntity nonExistentBrandEntity = new BrandEntity();
        nonExistentBrandEntity.setName("NonExistentBrand");
        when(brandEntityMapper.brandToBrandEntity(any(Brand.class))).thenReturn(nonExistentBrandEntity);
        when(brandRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());

        try {
            CompletableFuture<List<Car>> resultfuture = carService.updateBunchCars(cars);
            assertThrows(IllegalArgumentException.class, resultfuture::get);
        } catch (IllegalArgumentException e) {

            assertEquals("Brand: NonExistentBrand not yet registred", e.getMessage());

            verify(brandEntityMapper, times(1)).brandToBrandEntity(any(Brand.class));
            verify(brandRepository, times(1)).findByNameIgnoreCase(anyString());
            verify(carRepository, never()).existsById(anyInt());
            verify(carRepository, never()).save(any(CarEntity.class));
        }
    }

    @Test
    void addBunchCarsWhenIdNotFound(){
        Integer id = 77;
        car1.setId(id);
        List<Car> cars = Arrays.asList(car1);

        when(brandEntityMapper.brandToBrandEntity(any(Brand.class))).thenReturn(toyotaBrandEntity);
        when(brandRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(toyotaBrandEntity));
        when(carRepository.existsById(anyInt())).thenReturn(false);


        try {
            CompletableFuture<List<Car>> resultFuture = carService.updateBunchCars(cars);
            assertThrows(IllegalArgumentException.class,  resultFuture::get);

        } catch (IllegalArgumentException e) {
            assertEquals("Id: 77 does not exist", e.getMessage());

            verify(brandEntityMapper, times(1)).brandToBrandEntity(any(Brand.class));
            verify(brandRepository, times(1)).findByNameIgnoreCase(anyString());
            verify(carRepository, times(1)).existsById(anyInt());
            verify(carRepository, never()).save(any(CarEntity.class));
        }
    }

    @Test
    void addBunchCarsWhenIdNull(){

        car1.setId(null);
        List<Car> cars = Arrays.asList(car1);

        when(brandEntityMapper.brandToBrandEntity(any(Brand.class))).thenReturn(toyotaBrandEntity);
        when(brandRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(toyotaBrandEntity));

        try {
            CompletableFuture<List<Car>> resultFuture = carService.updateBunchCars(cars);
            assertThrows(IllegalArgumentException.class,  resultFuture::get);

        } catch (IllegalArgumentException e) {
            assertEquals("Id: null does not exist", e.getMessage());

            verify(brandEntityMapper, times(1)).brandToBrandEntity(any(Brand.class));
            verify(brandRepository, times(1)).findByNameIgnoreCase(anyString());
            verify(carRepository, never()).existsById(anyInt());
            verify(carRepository, never()).save(any(CarEntity.class));
        }
    }

    @Test
    void updateBunchCarsWhenEmptyList() throws Exception {

        List<Car> emptyCars = Arrays.asList();

        CompletableFuture<List<Car>> result = carService.updateBunchCars(emptyCars);
        List<Car> updatedCars = result.get();

        assertTrue(updatedCars.isEmpty());

        verify(brandEntityMapper, never()).brandToBrandEntity(any(Brand.class));
        verify(brandRepository, never()).findByNameIgnoreCase(anyString());
        verify(carRepository, never()).existsById(anyInt());
        verify(carRepository, never()).save(any(CarEntity.class));
    }

    @Test
    void deleteCarByIdSuccess() {
        Integer id = 1;

        when(carRepository.existsById(anyInt())).thenReturn(true);

        carService.deleteCarById(id);

        verify(carRepository).deleteById(id);
    }

    @Test
    void deleteCarByIdWhenIdNotFound(){
        Integer id = 77;

        when(carRepository.existsById(id)).thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> carService.deleteCarById(id));

        assertEquals("Car not found with Id: " + id, exception.getMessage());

        verify(carRepository, never()).deleteById(any());
    }

    @Test
    void deleteCarById_NullId_ThrowsIllegalArgumentException() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> carService.deleteCarById(null));

        assertEquals("Car not found with Id: null", exception.getMessage());

        verify(carRepository, never()).existsById(any());
        verify(carRepository, never()).deleteById(any());
    }

    @Test
    void getAllCarsSuccess() throws ExecutionException, InterruptedException {

        List<CarEntity> carEntityList = Arrays.asList(carEntity1, carEntity2);

        when(carRepository.findAll()).thenReturn(carEntityList);

        when(carEntityMapper.carEntityToCar(any(CarEntity.class))).thenReturn(car1,car2);

        CompletableFuture<List<Car>> resultFuture = carService.getAllCars();

        assertNotNull(resultFuture);
        List<Car> cars =  resultFuture.get();
        assertEquals(2, cars.size());
        assertTrue(cars.contains(car1));
        assertTrue(cars.contains(car2));

        verify(carRepository, times(1)).findAll();
        verify(carEntityMapper, times(2)).carEntityToCar(any(CarEntity.class));

    }

    @Test
    void getAllCarsWhenNoCarExists() throws ExecutionException, InterruptedException {

        when(carRepository.findAll()).thenReturn(Arrays.asList());

        CompletableFuture<List<Car>> result = carService.getAllCars();

        assertNotNull(result);
        List<Car> cars = result.get();
        assertTrue(cars.isEmpty());

        verify(carRepository, times(1)).findAll();
        verify(carEntityMapper, never()).carEntityToCar(any(CarEntity.class));
    }

}