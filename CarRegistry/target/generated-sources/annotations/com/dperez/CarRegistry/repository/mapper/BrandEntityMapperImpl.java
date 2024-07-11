package com.dperez.CarRegistry.repository.mapper;

import com.dperez.CarRegistry.repository.entity.BrandEntity;
import com.dperez.CarRegistry.service.model.Brand;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-11T22:18:07+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.8 (Oracle Corporation)"
)
@Component
public class BrandEntityMapperImpl implements BrandEntityMapper {

    @Override
    public Brand brandEntityToBrand(BrandEntity brandEntity) {
        if ( brandEntity == null ) {
            return null;
        }

        Brand.BrandBuilder brand = Brand.builder();

        brand.id( brandEntity.getId() );
        brand.name( brandEntity.getName() );
        brand.warranty( brandEntity.getWarranty() );
        brand.country( brandEntity.getCountry() );

        return brand.build();
    }

    @Override
    public BrandEntity brandToBrandEntity(Brand brand) {
        if ( brand == null ) {
            return null;
        }

        BrandEntity.BrandEntityBuilder brandEntity = BrandEntity.builder();

        brandEntity.id( brand.getId() );
        brandEntity.name( brand.getName() );
        brandEntity.warranty( brand.getWarranty() );
        brandEntity.country( brand.getCountry() );

        return brandEntity.build();
    }
}
