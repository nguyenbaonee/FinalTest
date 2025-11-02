package com.example.NodoTest.repo;

import com.example.NodoTest.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ProductImageRepo extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdIn(Set<Long> productIds);

    List<ProductImage> findByProduct_IdAndStatus(Long productId, String status);
}
