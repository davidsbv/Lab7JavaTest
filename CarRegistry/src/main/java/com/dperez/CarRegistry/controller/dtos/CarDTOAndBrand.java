package com.dperez.CarRegistry.controller.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarDTOAndBrand {

    private Integer id;
    private BrandDTO brand;
    private String model;
    private Integer mileage;
    private Double price;
    private Integer year;
    private String description;
    private String color;
    private String fuelType;
    private Integer numDoors;

}
