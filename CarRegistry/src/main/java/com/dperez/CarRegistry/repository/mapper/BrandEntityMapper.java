package com.dperez.CarRegistry.repository.mapper;

import com.dperez.CarRegistry.repository.entity.BrandEntity;
import com.dperez.CarRegistry.service.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BrandEntityMapper {

    BrandEntityMapper INSTANCE = Mappers.getMapper(BrandEntityMapper.class);

    Brand brandEntityToBrand(BrandEntity brandEntity);

    BrandEntity brandToBrandEntity(Brand brand);
}
