package com.example.NodoTest.mapper;

import com.example.NodoTest.dto.request.ProductRequest;
import com.example.NodoTest.dto.request.ProductSearch;
import com.example.NodoTest.dto.request.ProductUpdate;
import com.example.NodoTest.dto.response.ProductResponse;
import com.example.NodoTest.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toProduct(ProductRequest productRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct(ProductRequest productRequest, @MappingTarget Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct1(ProductUpdate productUpdate, @MappingTarget Product product);
    ProductResponse toProductResponse(Product product);

//    @Named("mapCategoryIds")
//    default List<Long> mapCategoryIds(List<ProductCategory> productCategories) {
//        if (productCategories == null) return Collections.emptyList();
//        return productCategories.stream()
//                .map(ProductCategory::getId)
//                .collect(Collectors.toList());
//    }
}
