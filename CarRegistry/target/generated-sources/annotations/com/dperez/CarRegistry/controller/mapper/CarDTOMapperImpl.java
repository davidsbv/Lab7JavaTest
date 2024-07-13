package com.dperez.CarRegistry.controller.mapper;

import com.dperez.CarRegistry.controller.dtos.CarDTO;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Car;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-13T12:03:21+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.8 (Oracle Corporation)"
)
@Component
public class CarDTOMapperImpl implements CarDTOMapper {

    @Override
    public CarDTO carToCarDTO(Car car) {
        if ( car == null ) {
            return null;
        }

        CarDTO carDTO = new CarDTO();

        carDTO.setBrand( carBrandName( car ) );
        carDTO.setId( car.getId() );
        carDTO.setModel( car.getModel() );
        carDTO.setMileage( car.getMileage() );
        carDTO.setPrice( car.getPrice() );
        carDTO.setYear( car.getYear() );
        carDTO.setDescription( car.getDescription() );
        carDTO.setColor( car.getColor() );
        carDTO.setFuelType( car.getFuelType() );
        carDTO.setNumDoors( car.getNumDoors() );

        return carDTO;
    }

    @Override
    public Car carDTOToCar(CarDTO carDTO) {
        if ( carDTO == null ) {
            return null;
        }

        Car car = new Car();

        car.setBrand( carDTOToBrand( carDTO ) );
        car.setId( carDTO.getId() );
        car.setModel( carDTO.getModel() );
        car.setMileage( carDTO.getMileage() );
        car.setPrice( carDTO.getPrice() );
        car.setYear( carDTO.getYear() );
        car.setDescription( carDTO.getDescription() );
        car.setColor( carDTO.getColor() );
        car.setFuelType( carDTO.getFuelType() );
        car.setNumDoors( carDTO.getNumDoors() );

        return car;
    }

    private String carBrandName(Car car) {
        if ( car == null ) {
            return null;
        }
        Brand brand = car.getBrand();
        if ( brand == null ) {
            return null;
        }
        String name = brand.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    protected Brand carDTOToBrand(CarDTO carDTO) {
        if ( carDTO == null ) {
            return null;
        }

        Brand brand = new Brand();

        brand.setName( carDTO.getBrand() );

        return brand;
    }
}
