package com.dperez.CarRegistry.service.impl;

import com.dperez.CarRegistry.repository.BrandRepository;
import com.dperez.CarRegistry.repository.CarRepository;
import com.dperez.CarRegistry.repository.entity.BrandEntity;
import com.dperez.CarRegistry.repository.entity.CarEntity;
import com.dperez.CarRegistry.repository.mapper.BrandEntityMapper;
import com.dperez.CarRegistry.repository.mapper.CarEntityMapper;
import com.dperez.CarRegistry.service.CarService;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Car;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CarServiceImpl implements CarService {

    private static final String ALREADY_EXISTS = "already exists";
    private static final String DOES_NOT_EXIST = "does not exist";
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CarEntityMapper carEntityMapper;
    @Autowired
    private BrandEntityMapper brandEntityMapper;

    @Override
    public Car addCar(Car car) throws IllegalArgumentException {

        // Verifica si la Id ya existe. Lanza una excepción en caso afirmativo.
        if (car.getId() != null && carRepository.existsById(car.getId())) {

            throw new IllegalArgumentException("The Id " + car.getId() + " " + ALREADY_EXISTS);
        }

        // Verificar si la marca existe
        Optional<BrandEntity> brandEntityOptional = brandRepository.findByNameIgnoreCase(car.getBrand().getName());

        if (brandEntityOptional.isEmpty()) {
            throw new IllegalArgumentException("Brand " + car.getBrand().getName() + " " + DOES_NOT_EXIST);
        }

        // Obtener la BrandEntity existente y pasar a Brand
        BrandEntity brandEntity = brandEntityOptional.get();
        Brand brand = brandEntityMapper.brandEntityToBrand(brandEntity);

        // Asociar la BrandEntitiy existente al car
        car.setBrand(brand);

        // Se pasa car a carEntity para guardar
        CarEntity carEntity = carEntityMapper.carToCarEntity(car);
        // carEntity.setBrand((brand)); // Se asocia la brand existente

        // Se guarda la CarEntity en la base de datos
        CarEntity savedCarEntity = carRepository.save(carEntity);

        // Se devuelve el coche guardado como modelo de dominio
        return carEntityMapper.carEntityToCar(savedCarEntity);
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<Car>> addBunchCars(List<Car> cars) throws IllegalArgumentException {
        long starTime = System.currentTimeMillis();

        try {
            // Verificar si la id existe y si la marca está en la base de datos
            List<Car> addedCars = cars.stream().map(car -> {
                if ((car.getId() != null) && carRepository.existsById(car.getId())) {
                    throw new IllegalArgumentException("The Id " + car.getId() + " " + ALREADY_EXISTS);
                }
                // Se obtienen las marcas de los coches a actualizar
                Optional<BrandEntity> brandEntityOptional = brandRepository.findByNameIgnoreCase(car.getBrand().getName());

                if (brandEntityOptional.isEmpty()) {
                    throw new IllegalArgumentException("Brand " + car.getBrand().getName() + " " + DOES_NOT_EXIST);
                }

                // Toma la BrandEntity, se le asigna a cada CarEntity que se ha transformado previamente de Car
                BrandEntity brandEntity = brandEntityOptional.get();
                CarEntity carEntity = carEntityMapper.carToCarEntity(car);
                carEntity.setBrand(brandEntity);

                // Se guarda el CarEntity con los datos validados, en savedCar para retornarlos al stream como Car
                CarEntity savedCarEntity = carRepository.save(carEntity);

                return carEntityMapper.carEntityToCar(savedCarEntity);
            }).toList();

            long endTime = System.currentTimeMillis();
            log.info("Total time: " + (endTime - starTime) + "ms");

            // Se devulven los coches guardados
            return CompletableFuture.completedFuture(addedCars);
        } catch (IllegalArgumentException e) {
            log.error("Error adding several cars");
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public Car getCarById(Integer id) {

        // Búsqueda de car por id
        Optional<CarEntity> carEntityOptional = carRepository.findById(id);

        log.info("Searching id: " + id);
        // Si se encuentra devuelve el objeto car. En caso contrario devuelve null.
//        return carEntityOptional.map(CarEntityMapper.INSTANCE::carEntityToCar).orElse(null); ERROR EN LOS TEST
        return carEntityOptional.map(carEntity -> carEntityMapper.carEntityToCar(carEntity)).orElse(null);
    }


    @Override
    public Car updateCarById(Integer id, Car car) throws IllegalArgumentException {

        // Verifica si la Marca del objeto Car existe
        Optional<BrandEntity> brandEntityOptional = brandRepository.findByNameIgnoreCase(car.getBrand().getName());

        if (brandEntityOptional.isEmpty()) {
            log.error("Unknown id");
            throw new IllegalArgumentException("Brand with name " + car.getBrand() + " " + DOES_NOT_EXIST);
        }

        // Verifica si la Id existe. Lanza excepción en caso negativo. En caso afirmativo actualiza los datos
        if (id == null || !carRepository.existsById(id)) {
            log.error("Unknown id");
            throw new IllegalArgumentException("Id " + id + " " + DOES_NOT_EXIST);
        } else {
            // Se obtiene la BrandEntity existente y se asocia a la carEntity a actualizar
            BrandEntity brandEntity = brandEntityOptional.get();
            log.info("Marca " + brandEntity + " encontrada");
            CarEntity carEntity = carEntityMapper.carToCarEntity(car);

            // Seteo de la id y la marca
            carEntity.setId(id);
            carEntity.setBrand(brandEntity);

            // Actualiza los datos y devuelve el objeto actualizado.
            CarEntity updatedCarEntity = carRepository.save(carEntity);
            return carEntityMapper.carEntityToCar(updatedCarEntity);
        }

    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<Car>> updateBunchCars(List<Car> cars) throws IllegalArgumentException {
        long starTime = System.currentTimeMillis();

        // Se guardan los Car actualizados haciendo las comprobaciones de marca e id existentes.
        List<Car> updatedCars = cars.stream().map(car -> {

            // Pasar de Brand a BrandEntity y comprobar que está dada de alta en la base de datos
            BrandEntity brandEntity = brandEntityMapper.brandToBrandEntity(car.getBrand());
            Optional<BrandEntity> brandEntityOptional = brandRepository.findByNameIgnoreCase(brandEntity.getName());

            // Comprobación de Brand
            if (brandEntityOptional.isEmpty()) {
                throw new IllegalArgumentException("Brand: " + brandEntity.getName() + " not yet registred");
            }
            // Comprobación de id
            if ((car.getId() == null) || (!carRepository.existsById(car.getId()))) {
                throw new IllegalArgumentException("Id: " + car.getId() + " " + DOES_NOT_EXIST);
            }

            // Si existen id y brand de cada car se toman los datos de Brand de la base de datos
            brandEntity = brandEntityOptional.get();

            // Pasamos los Car a CarEntity
            CarEntity carEntity = carEntityMapper.carToCarEntity(car);

            // Se setea BrandEntity para su CarEntity
            carEntity.setBrand(brandEntity);

            // Se actualizan los datos
            CarEntity updatedCarEntity = carRepository.save(carEntity);

            // Se devuelve el Car actualizado
            return carEntityMapper.carEntityToCar(updatedCarEntity);
        }).toList();

        long endTime = System.currentTimeMillis();
        log.info("Total time: " + (endTime - starTime) + "ms");

        return CompletableFuture.completedFuture(updatedCars);
    }


    @Override
    public void deleteCarById(Integer id) throws IllegalArgumentException {

        // Si la id existe borra el coche. En caso contrario lanza error.
        if (id != null && carRepository.existsById(id)) {
            carRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Car not found with Id: " + id);
        }
    }


    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<Car>> getAllCars() {

        long starTime = System.currentTimeMillis();

        // Se obtienen en una lista todos los objetos de tipo CarEntity y se mapean a tipo Car
        //MODIFICADO CarEntityMapper.INSTANCE::carEntityToCar POR carEntityMapper::carEntityToCar
        // mejora la velocidad porque no tiene que cargar toda la clase por debajo
        List<Car> allCars = carRepository.findAll().stream()
                .map(carEntity -> carEntityMapper.carEntityToCar(carEntity))
                .toList();

        long endTime = System.currentTimeMillis();
        log.info("Total time: " + (endTime - starTime) + "ms");


        // Se devuelve el resultado
        return CompletableFuture.completedFuture(allCars);
    }

}
