package com.dperez.CarRegistry.controller.mapper;

import com.dperez.CarRegistry.controller.dtos.CarDTOAndBrand;
import com.dperez.CarRegistry.service.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CarDTOAndBrandMapper {

    CarDTOAndBrandMapper INSTANCE = Mappers.getMapper(CarDTOAndBrandMapper.class);

    // @Mapping(source = "brand", target = "brand")
    CarDTOAndBrand carToCarDTOAndBrand(Car car);

    // @Mapping(source = "brand", target = "brand")
    Car carDTOAndBrandToCar(CarDTOAndBrand carDTOAndBrand);
}