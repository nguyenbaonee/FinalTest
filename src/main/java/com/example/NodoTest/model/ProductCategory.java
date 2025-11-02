package com.example.NodoTest.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_category",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "category_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 1)
    private String status;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = "1";
        }
    }
}

