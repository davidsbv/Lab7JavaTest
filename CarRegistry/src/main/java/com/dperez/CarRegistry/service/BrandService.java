package com.dperez.CarRegistry.service;

import com.dperez.CarRegistry.service.model.Brand;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BrandService {

    Brand addBrand (Brand brand);

//    CompletableFuture<List<Brand>> addBunchBrands(List<Brand> brands);

    Brand getBrandById (Integer id);

    Brand updateBrandByName (String brandName, Brand brand);

//    CompletableFuture<List<Brand>> updateBunchBrands (List<Brand> brands);

    void deleteCarById (Integer id);

    CompletableFuture<List<Brand>> getAllBrands();
}
