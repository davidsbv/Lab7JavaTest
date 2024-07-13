package com.dperez.CarRegistry.repository.mapper;

import com.dperez.CarRegistry.repository.entity.BrandEntity;
import com.dperez.CarRegistry.repository.entity.CarEntity;
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
public class CarEntityMapperImpl implements CarEntityMapper {

    @Override
    public Car carEntityToCar(CarEntity carEntity) {
        if ( carEntity == null ) {
            return null;
        }

        Car car = new Car();

        car.setId( carEntity.getId() );
        car.setBrand( brandEntityToBrand( carEntity.getBrand() ) );
        car.setModel( carEntity.getModel() );
        car.setMileage( carEntity.getMileage() );
        car.setPrice( carEntity.getPrice() );
        car.setYear( carEntity.getYear() );
        car.setDescription( carEntity.getDescription() );
        car.setColor( carEntity.getColor() );
        car.setFuelType( carEntity.getFuelType() );
        car.setNumDoors( carEntity.getNumDoors() );

        return car;
    }

    @Override
    public CarEntity carToCarEntity(Car car) {
        if ( car == null ) {
            return null;
        }

        CarEntity carEntity = new CarEntity();

        carEntity.setId( car.getId() );
        carEntity.setBrand( brandToBrandEntity( car.getBrand() ) );
        carEntity.setModel( car.getModel() );
        carEntity.setMileage( car.getMileage() );
        carEntity.setPrice( car.getPrice() );
        carEntity.setYear( car.getYear() );
        carEntity.setDescription( car.getDescription() );
        carEntity.setColor( car.getColor() );
        carEntity.setFuelType( car.getFuelType() );
        carEntity.setNumDoors( car.getNumDoors() );

        return carEntity;
    }

    protected Brand brandEntityToBrand(BrandEntity brandEntity) {
        if ( brandEntity == null ) {
            return null;
        }

        Brand brand = new Brand();

        brand.setId( brandEntity.getId() );
        brand.setName( brandEntity.getName() );
        brand.setWarranty( brandEntity.getWarranty() );
        brand.setCountry( brandEntity.getCountry() );

        return brand;
    }

    protected BrandEntity brandToBrandEntity(Brand brand) {
        if ( brand == null ) {
            return null;
        }

        BrandEntity brandEntity = new BrandEntity();

        brandEntity.setId( brand.getId() );
        brandEntity.setName( brand.getName() );
        brandEntity.setWarranty( brand.getWarranty() );
        brandEntity.setCountry( brand.getCountry() );

        return brandEntity;
    }
}
