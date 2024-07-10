package com.dperez.CarRegistry.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "brand")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Integer warranty;
    private String country;

    // Conjunto de objetos CarEntity asociados con una entidad BrandEntity
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private Set<CarEntity> cars;
}
