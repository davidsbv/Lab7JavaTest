package com.dperez.CarRegistry.service.impl;

import com.dperez.CarRegistry.repository.BrandRepository;
import com.dperez.CarRegistry.repository.entity.BrandEntity;
import com.dperez.CarRegistry.repository.mapper.BrandEntityMapper;
import com.dperez.CarRegistry.service.BrandService;
import com.dperez.CarRegistry.service.model.Brand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public Brand addBrand(Brand brand) throws IllegalArgumentException {
        Optional<BrandEntity> brandEntityOptional = brandRepository.findByNameIgnoreCase(brand.getName());

        if(brandEntityOptional.isPresent()){
            log.error("Repeated brand");
            throw new IllegalArgumentException("Brand: " + brand.getName() + " already exists");
        }
        if((brand.getId() != null) && (brandRepository.existsById(brand.getId()))){
            log.error("Id error");
            throw new IllegalArgumentException("Id: " + brand.getId() + " already exists");
        }

        BrandEntity brandEntity = BrandEntityMapper.INSTANCE.brandToBrandEntity(brand);
        BrandEntity brandSaved = brandRepository.save(brandEntity);
        return BrandEntityMapper.INSTANCE.brandEntityToBrand(brandSaved);
    }

    @Override
    public Brand getBrandById(Integer id) {

        // Búsqueda de brand por id
        Optional<BrandEntity> brandEntityOptional = brandRepository.findById(id);
        
        // Devuelve Brand si se encuentra y null si no hay coincidencia.
        return brandEntityOptional.map(BrandEntityMapper.INSTANCE::brandEntityToBrand)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
    }

    @Override
    public Brand updateBrandByName(String brandName, Brand brand) throws IllegalArgumentException{
        // Búsqueda de brand por nombre
        Optional<BrandEntity> brandEntityNameOptional = brandRepository.findByNameIgnoreCase(brandName);

        return brandEntityNameOptional.map(brandEntity -> {
            // Se mapea Brand a BrandEntity
            BrandEntity brandEntityToUpdate = BrandEntityMapper.INSTANCE.brandToBrandEntity(brand);
            // Seteo de la Id para actualizar
            brandEntityToUpdate.setId(brandEntity.getId());
            // Actualización
            brandRepository.save(brandEntityToUpdate);
            // Mapeo de BrandEntity a Brand y retorno de éste actualizado
            return BrandEntityMapper.INSTANCE.brandEntityToBrand(brandEntityToUpdate);

        }).orElseThrow(() -> new IllegalArgumentException("Brand not found"));
    }

    @Override
    public void deleteCarById(Integer id) {
        Optional<BrandEntity> brandEntityOptional = brandRepository.findById(id);

        brandEntityOptional.map(brandEntity -> {
            brandRepository.deleteById(brandEntity.getId());
            return brandEntity;
        }).orElseThrow(() -> new IllegalArgumentException("Brand whith id: " + id + " not found"));

    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<Brand>> getAllBrands() {

        List<Brand> allBrands = brandRepository.findAll()
                .stream().map(BrandEntityMapper.INSTANCE::brandEntityToBrand).toList();
        return CompletableFuture.completedFuture(allBrands);
    }
}
