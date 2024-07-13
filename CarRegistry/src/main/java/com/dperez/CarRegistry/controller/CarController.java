package com.dperez.CarRegistry.controller;

import com.dperez.CarRegistry.controller.dtos.CarDTO;
import com.dperez.CarRegistry.controller.dtos.CarDTOAndBrand;
import com.dperez.CarRegistry.controller.mapper.CarDTOAndBrandMapper;
import com.dperez.CarRegistry.controller.mapper.CarDTOMapper;
import com.dperez.CarRegistry.service.CarService;
import com.dperez.CarRegistry.service.model.Car;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @Autowired
    private CarDTOMapper carDTOMapper;

    @Autowired
    private CarDTOAndBrandMapper carDTOAndBrandMapper;

    @PostMapping("add-car")
//    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> addCar(@RequestBody CarDTO carDTO){

        try {
            // Se convierte carDTO a Car y se utiliza en la llmada al método addCar.
            // Cuando se guarda se devuelve en newCarDTO  y se muestra la respuesta
            Car car = CarDTOMapper.INSTANCE.carDTOToCar(carDTO);
            Car newCar = carService.addCar(car);
            CarDTOAndBrand newCarDTOAndBrand = carDTOAndBrandMapper.INSTANCE.carToCarDTOAndBrand(newCar);
            log.info("New Car added");
            return ResponseEntity.ok(newCarDTOAndBrand);

        } catch (IllegalArgumentException e) {
            // Error por Id ya existente.
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e){
            log.error("Error while adding new car");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Añadir lista de coches
    @PostMapping(value = "/add-bunch", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<List<CarDTOAndBrand>> addBunchCars(@RequestBody List<CarDTO> carDTOs){

        // Mapear los carDTOs a Car
        List<Car> carsToAdd = carDTOs.stream().map(car -> carDTOMapper.carDTOToCar(car)).toList();

        // LLamada al servicio asíncrono
        CompletableFuture<List<Car>> futureCars = carService.addBunchCars(carsToAdd);

        try {
            // Espera a que acabe el método asíncrono
            List<Car> addedCars = futureCars.get();
            List<CarDTOAndBrand> carsDTOAndBrand = addedCars.stream()
                    .map(car -> carDTOAndBrandMapper.carToCarDTOAndBrand(car)).toList();
//            return ResponseEntity.ok(carsDTOAndBrand);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(carsDTOAndBrand);


        } catch (IllegalArgumentException e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Obtener información de un coche por id
    @GetMapping(value = "get-car/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getCarById(@PathVariable Integer id){

        // Se busca la id solicitada. Si existe se devuelve la información del coche y la marca.
        // Si no devuelve mensaje de error.
        Car car = carService.getCarById(id);
        if (car != null){
            log.info("Car info loaded");
            CarDTOAndBrand carDTOAndBrand = carDTOAndBrandMapper.carToCarDTOAndBrand(car);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(carDTOAndBrand);
        }
        else {
            log.error("Id does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found");
        }
    }

    // Actualizar un coche
    @PutMapping("update-car/{id}")
//    @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<?> updateCarById(@PathVariable Integer id, @RequestBody CarDTO carDto){

        try {
            // Mapear carDTO a Car y llamada al método updateCarById
            Car car = carDTOMapper.carDTOToCar(carDto);
            Car carToUpdate = carService.updateCarById(id, car);

            // Mapear Car a CarDTO y devolver CarDTO actualizado
            CarDTOAndBrand carUpdated = carDTOAndBrandMapper.carToCarDTOAndBrand(carToUpdate);
            log.info("Car updated");
            return ResponseEntity.ok(carUpdated);

        } catch (IllegalArgumentException e) {  // Error en la id pasada
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e){
            log.error("Error while updating car");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    // Actualizar lista de coches
    @PutMapping("update-bunch")
    @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<List<CarDTOAndBrand>> updateBunch(@RequestBody List<CarDTO> carDTOs){

        // Mapeo de carDTOs a Car
        List<Car> cars = carDTOs.stream().map(carDTOMapper.INSTANCE::carDTOToCar).toList();

        // Llamada al método asíncrono
        CompletableFuture<List<Car>> futureCars = carService.updateBunchCars(cars);

        try {
            // Espera hasta tener el resultado
            List<Car> updatedCars = futureCars.get();

            // Mapeo del resultado
            List<CarDTOAndBrand> updatedCarDTOsAndBrand = updatedCars.stream()
                    .map(carDTOAndBrandMapper.INSTANCE::carToCarDTOAndBrand).toList();

            log.info("Updating several cars");

            // Retorno del resultado de la actualización
            return ResponseEntity.ok(updatedCarDTOsAndBrand);

        } catch (IllegalArgumentException e) {
            log.error("Error updating cars");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Borrar un coche por id
    @DeleteMapping("delete-car/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> deleteCarById(@PathVariable Integer id){

        try {
            carService.deleteCarById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted Car with Id: " + id);

        } catch (IllegalArgumentException e) { // Error en la id pasada
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e){
            log.error("Deleting car error");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Recuperar la información de todos los coches
    @GetMapping("get-all")
//    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<CarDTOAndBrand>> getAllCars(){

        // Mapea la lista con objetos Car a una lista con objetos carDTOAndBrand y muestra su resultado.
        // Bloquea hasta que la llamada asincrónica se complete
        try {
            List<Car> carRecovered =  carService.getAllCars().get();

            // Mapea la lista con objetos Car a una lista con objetos CarDTOAndBrand
            List<CarDTOAndBrand> carDTOsAndBrand = carRecovered.stream()
                        .map(carDTOAndBrandMapper.INSTANCE::carToCarDTOAndBrand).toList();

            // Devuelve la respuesta con la lista de CarDTOAndBrand
            return ResponseEntity.ok(carDTOsAndBrand);
        } catch (InterruptedException | ExecutionException e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }
}
