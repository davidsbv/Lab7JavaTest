package com.dperez.CarRegistry.service;

import com.dperez.CarRegistry.service.model.Car;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface CarService {

    Car addCar(Car car) throws DataIntegrityViolationException;
    CompletableFuture<List<Car>> addBunchCars(List<Car> cars);
    Car getCarById(Integer id);
    Car updateCarById(Integer id, Car car);
    CompletableFuture<List<Car>> updateBunchCars(List<Car> cars);
    void deleteCarById(Integer id);
    CompletableFuture<List<Car>> getAllCars() throws IllegalArgumentException;
}
