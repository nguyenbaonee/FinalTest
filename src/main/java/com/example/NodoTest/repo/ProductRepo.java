package com.example.NodoTest.repo;

import com.example.NodoTest.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepo extends JpaRepository<Product, Long> {
    boolean existsByProductCode(String productCode);

    @Query("""
    SELECT p FROM Product p
    WHERE (:productCode IS NULL OR p.productCode = :productCode)
      AND (:description IS NULL OR p.description = :description)
      AND (:createdFrom IS NULL OR p.createdDate >= :createdFrom)
      AND (:createdTo IS NULL OR p.createdDate <= :createdTo)
      AND p.status = '1'
""")
    Page<Product> searchProducts(@Param("productCode") String productCode,
                                 @Param("description") String description,
                                 @Param("createdFrom") Date createdFrom,
                                 @Param("createdTo") Date createdTo,
                                 Pageable pageable);

        @Query("""
        SELECT DISTINCT p.id
        FROM Product p
        LEFT JOIN p.productCategories pc
        WHERE p.status = '1'
          AND (:name IS NULL OR LOWER(p.name) LIKE :name)
          AND (:productCode IS NULL OR LOWER(p.productCode) LIKE :productCode)
          AND (:createdFrom IS NULL OR p.createdDate >= :createdFrom)
          AND (:createdTo IS NULL OR p.createdDate <= :createdTo)
          AND (:categoryIds IS NULL OR pc.category.id IN :categoryIds AND pc.status = '1')
    """)
        Page<Long> searchIds(Pageable pageable,
                             @Param("name") String name,
                             @Param("productCode") String productCode,
                             @Param("createdFrom") Date createdFrom,
                             @Param("createdTo") Date createdTo,
                             @Param("categoryIds") List<Long> categoryIds);

    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.images i
        LEFT JOIN FETCH p.productCategories pc
        LEFT JOIN FETCH pc.category c
        WHERE p.id IN :ids AND i.status = '1'
    """)
    List<Product> findAllWithImagesAndCategories(@Param("ids") List<Long> ids);

    Optional<Product> findByIdAndStatus(Long id, String status);
}
