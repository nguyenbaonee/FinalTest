package com.example.NodoTest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "category_code", unique = true, nullable = false)
    private String categoryCode;

    private String description;

    @Column(length = 1)
    private String status ;

    @OneToMany(mappedBy = "category")
    private List<ProductCategory> productCategories;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<CategoryImage> images;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = "1";
        }
    }
}

