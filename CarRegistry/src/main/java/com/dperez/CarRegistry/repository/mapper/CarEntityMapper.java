package com.dperez.CarRegistry.repository.mapper;

import com.dperez.CarRegistry.repository.entity.CarEntity;
import com.dperez.CarRegistry.service.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CarEntityMapper {

    CarEntityMapper INSTANCE = Mappers.getMapper(CarEntityMapper.class);

    // @Mapping(source = "brand", target = "brand")
    Car carEntityToCar(CarEntity carEntity);

    // @Mapping(source = "brand", target = "brandEntity")
    CarEntity carToCarEntity(Car car);
}