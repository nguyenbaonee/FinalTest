package com.example.NodoTest.repo;

import com.example.NodoTest.model.Category;
import com.example.NodoTest.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    boolean existsByCategoryCode(String categoryCode);

    @Query("""
    SELECT c.id FROM Category c
    WHERE (:name IS NULL OR LOWER(c.name) LIKE :name ESCAPE '\\')
    AND (:categoryCode IS NULL OR c.categoryCode = :categoryCode)
    AND (:createdFrom IS NULL OR DATE(c.createdDate) >= DATE(:createdFrom))
    AND (:createdTo IS NULL OR DATE(c.createdDate) <= DATE(:createdTo))
    AND c.status = '1'
""")
    Page<Long> searchIds(@Param("name") String name,
                         @Param("categoryCode") String categoryCode,
                         @Param("createdFrom") Date createdFrom,
                         @Param("createdTo") Date createdTo,
                         Pageable paageabl);

    @Query("""
    SELECT c FROM Category c
    LEFT JOIN FETCH c.images
    WHERE c.id IN :ids
""")
    List<Category> findAllByIdWithImages(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.status = '0', c.modifiedDate = CURRENT_TIMESTAMP WHERE c.id = :id")
    void updateCategoryStatus(@Param("id") Long id);

    Optional<Category> findByIdAndStatus(Long id, String status);
}
