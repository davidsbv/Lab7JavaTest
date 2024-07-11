package com.dperez.CarRegistry.controller.mapper;

import com.dperez.CarRegistry.controller.dtos.BrandDTO;
import com.dperez.CarRegistry.controller.dtos.CarDTOAndBrand;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Car;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-11T22:15:43+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.8 (Oracle Corporation)"
)
@Component
public class CarDTOAndBrandMapperImpl implements CarDTOAndBrandMapper {

    @Override
    public CarDTOAndBrand carToCarDTOAndBrand(Car car) {
        if ( car == null ) {
            return null;
        }

        CarDTOAndBrand carDTOAndBrand = new CarDTOAndBrand();

        carDTOAndBrand.setId( car.getId() );
        carDTOAndBrand.setBrand( brandToBrandDTO( car.getBrand() ) );
        carDTOAndBrand.setModel( car.getModel() );
        carDTOAndBrand.setMileage( car.getMileage() );
        carDTOAndBrand.setPrice( car.getPrice() );
        carDTOAndBrand.setYear( car.getYear() );
        carDTOAndBrand.setDescription( car.getDescription() );
        carDTOAndBrand.setColor( car.getColor() );
        carDTOAndBrand.setFuelType( car.getFuelType() );
        carDTOAndBrand.setNumDoors( car.getNumDoors() );

        return carDTOAndBrand;
    }

    @Override
    public Car carDTOAndBrandToCar(CarDTOAndBrand carDTOAndBrand) {
        if ( carDTOAndBrand == null ) {
            return null;
        }

        Car car = new Car();

        car.setId( carDTOAndBrand.getId() );
        car.setBrand( brandDTOToBrand( carDTOAndBrand.getBrand() ) );
        car.setModel( carDTOAndBrand.getModel() );
        car.setMileage( carDTOAndBrand.getMileage() );
        car.setPrice( carDTOAndBrand.getPrice() );
        car.setYear( carDTOAndBrand.getYear() );
        car.setDescription( carDTOAndBrand.getDescription() );
        car.setColor( carDTOAndBrand.getColor() );
        car.setFuelType( carDTOAndBrand.getFuelType() );
        car.setNumDoors( carDTOAndBrand.getNumDoors() );

        return car;
    }

    protected BrandDTO brandToBrandDTO(Brand brand) {
        if ( brand == null ) {
            return null;
        }

        BrandDTO brandDTO = new BrandDTO();

        brandDTO.setId( brand.getId() );
        brandDTO.setName( brand.getName() );
        brandDTO.setWarranty( brand.getWarranty() );
        brandDTO.setCountry( brand.getCountry() );

        return brandDTO;
    }

    protected Brand brandDTOToBrand(BrandDTO brandDTO) {
        if ( brandDTO == null ) {
            return null;
        }

        Brand brand = new Brand();

        brand.setId( brandDTO.getId() );
        brand.setName( brandDTO.getName() );
        brand.setWarranty( brandDTO.getWarranty() );
        brand.setCountry( brandDTO.getCountry() );

        return brand;
    }
}
