package com.example.NodoTest.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Double price;

    @Column(name = "product_code", unique = true, nullable = false)
    private String productCode;

    private Long quantity;

    @Column(length = 1)
    private String status; // 1=active, 0=deleted

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private List<ProductCategory> productCategories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private List<ProductImage> images;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = "1";
        }
    }
}

