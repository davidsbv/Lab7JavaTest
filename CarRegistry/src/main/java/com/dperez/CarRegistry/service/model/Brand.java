package com.dperez.CarRegistry.service.model;

import lombok.*;

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


