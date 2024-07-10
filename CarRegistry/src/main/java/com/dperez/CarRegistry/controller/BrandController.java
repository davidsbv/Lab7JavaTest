package com.dperez.CarRegistry.controller;

import com.dperez.CarRegistry.controller.dtos.BrandDTO;
import com.dperez.CarRegistry.controller.mapper.BrandDTOMapper;
import com.dperez.CarRegistry.service.BrandService;
import com.dperez.CarRegistry.service.model.Brand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandDTOMapper brandDTOMapper;

    @PostMapping("add")
    public ResponseEntity<?> addBrand(@RequestBody BrandDTO brandDTO){
        try {
            Brand brand = BrandDTOMapper.INSTANCE.brandDTOToBrand(brandDTO);
            Brand newBrand = brandService.addBrand(brand);

            log.info("Brand added");
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BrandDTOMapper.INSTANCE.brandToBrandDTO(newBrand));

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
           return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getBrandById(@PathVariable Integer id){
        try {
            Brand updatedBrand = brandService.getBrandById(id);
            log.info("Recovering brand info");
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BrandDTOMapper.INSTANCE.brandToBrandDTO(updatedBrand));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e){
            log.error("Error getting brand info");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("update/{brandName}")
    public ResponseEntity<?> updateBrandByName(@PathVariable String brandName, @RequestBody BrandDTO brandDTO){

        try {
            Brand updatedBrand = brandService.updateBrandByName(brandName, BrandDTOMapper
                            .INSTANCE.brandDTOToBrand(brandDTO));
            return ResponseEntity.status(HttpStatus.OK).body(updatedBrand);
        } catch (IllegalArgumentException e) {
            log.error("Error in Brand Name");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error while updating brand");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteBrandById(@PathVariable Integer id){
        if (id == null){
            log.error("No id");
            return ResponseEntity.notFound().build();
        }
        else {
            brandService.deleteCarById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Brand with id " + id + " deleted");
        }
    }

    @GetMapping("get-all")
    public ResponseEntity<List<BrandDTO>> getAllBrands(){

        // Recuperar las marcas
        CompletableFuture<List<Brand>> allBrands = brandService.getAllBrands();

        try {
            // Esperar a que se complete el método asíncrono (Blocking)
            List<Brand> recoveredBrands = allBrands.get();

           // Mapeo de Brand a BrandDTO
            List<BrandDTO> allBrandDTOs = recoveredBrands.stream()
                    .map(brandDTOMapper.INSTANCE::brandToBrandDTO).toList();

            return ResponseEntity.ok(allBrandDTOs);

        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.internalServerError().build();
        }
        // CODIGO CUANDO SE DEVUELVE UN CompletableFuture
//        return allBrands.thenApply(brands -> {
//             brands.stream().map(BrandDTOMapper.INSTANCE::brandToBrandDTO).toList();
//             // En caso de querer conservar el valor de la lista mapeada podemos utilizar las dos líneas comentadas
////            List<BrandDTO> brandDTOS = brands.stream()
////                    .map(BrandDTOMapper.INSTANCE::brandToBrandDTO).toList();
//            log.info("Recovering all Brands");
//            return ResponseEntity.status(HttpStatus.OK).body(brands);
//        });

    }

}
