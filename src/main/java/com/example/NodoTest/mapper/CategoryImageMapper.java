package com.example.NodoTest.mapper;

import com.example.NodoTest.dto.request.CategoryImageRequest;
import com.example.NodoTest.dto.request.CategoryRequest;
import com.example.NodoTest.dto.response.CategoryImageResponse;
import com.example.NodoTest.model.CategoryImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryImageMapper {

    CategoryImage toCategoryImage(CategoryRequest categoryRequest);

    List<CategoryImage> toCategoryImageRequestList(List<CategoryImageRequest> categoryImageRequests);

    @Mapping(source = "categoryImage.category.id", target = "categoryId")
    CategoryImageResponse toCategoryImageResponse(CategoryImage categoryImage);
    List<CategoryImageResponse> toCategoryImageResponseList(List<CategoryImage> categoryImages);
}
