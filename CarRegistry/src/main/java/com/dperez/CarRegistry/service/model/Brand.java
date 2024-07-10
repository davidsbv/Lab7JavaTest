package com.dperez.CarRegistry.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Brand {

    private Integer id;
    private String name;
    private Integer warranty;
    private String country;
}


