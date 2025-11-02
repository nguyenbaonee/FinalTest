package com.example.NodoTest.repo;

import com.example.NodoTest.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepo extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findByProduct_Id(Long productId);
}
