package com.dperez.CarRegistry.controller.mapper;

import com.dperez.CarRegistry.controller.dtos.BrandDTO;
import com.dperez.CarRegistry.service.model.Brand;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-13T12:03:21+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.8 (Oracle Corporation)"
)
@Component
public class BrandDTOMapperImpl implements BrandDTOMapper {

    @Override
    public BrandDTO brandToBrandDTO(Brand brand) {
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

    @Override
    public Brand brandDTOToBrand(BrandDTO brandDTO) {
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
