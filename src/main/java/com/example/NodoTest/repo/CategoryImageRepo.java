package com.example.NodoTest.repo;

import com.example.NodoTest.model.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryImageRepo extends JpaRepository<CategoryImage, Long> {
    @Modifying
    @Query("""
    update CategoryImage cti set cti.status = '0'
    where cti.id in :ids and cti.category.id = :categoryId
""")
    int deleteCategory(@Param("ids") List<Long> ids, @Param("categoryId") Long categoryId);

    @Modifying
    @Query("UPDATE CategoryImage ci SET ci.status = '0' WHERE ci.category.id = :id")
    int updateCategoryImagesStatus(@Param("id") Long categoryId);

    List<CategoryImage> findByCategory_IdIn(List<Long> categoryIds);
}
