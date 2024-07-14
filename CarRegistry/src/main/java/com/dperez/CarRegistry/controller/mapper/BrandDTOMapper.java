package com.dperez.CarRegistry.controller.mapper;

import com.dperez.CarRegistry.controller.dtos.BrandDTO;
import com.dperez.CarRegistry.service.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BrandDTOMapper {

    BrandDTOMapper INSTANCE = Mappers.getMapper(BrandDTOMapper.class);

    BrandDTO brandToBrandDTO(Brand brand);

    Brand brandDTOToBrand(BrandDTO brandDTO);
}
