package com.example.NodoTest.mapper;

import com.example.NodoTest.dto.request.CateUpdate;
import com.example.NodoTest.dto.request.CategoryCreate;
import com.example.NodoTest.dto.request.CategoryRequest;
import com.example.NodoTest.dto.response.CategoryImageResponse;
import com.example.NodoTest.dto.response.CategoryResponse;
import com.example.NodoTest.model.Category;
import com.example.NodoTest.model.CategoryImage;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryImage.class})
public interface CategoryMapper {

    Category toCategory1(CategoryCreate categoryCreate);

    Category toCategory(CategoryRequest cacheRequest);

    CategoryResponse toCategoryResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategory(CateUpdate cateUpdate,@MappingTarget Category category);

    @AfterMapping
    default void linkImages(@MappingTarget Category category) {
        if (category.getImages() != null) {
            category.getImages().forEach(cateImg -> cateImg.setCategory(category));
        }
    }

//    @BeforeMapping
//    default void imagesResponse(Category category,@MappingTarget CategoryResponse categoryResponse) {
//        for(CategoryImage img : category.getImagesId()) {
//            if(img.getStatus().equals("1")) {
//                categoryResponse.getImagesId().add(img);
//            }
//        }
//    }
}
