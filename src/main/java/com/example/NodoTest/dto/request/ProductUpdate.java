package com.example.NodoTest.dto.request;

import com.example.NodoTest.common.UniqueCustom;
import com.example.NodoTest.model.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdate {
    @Size(max = 200,message = "Name can have maximum 200 characters")
    private String name;
    @UniqueCustom(entity = Product.class, field = "productCode", message = "Product code must be unique")
    private String productCode;
    private String description;
    @Min(value = 0,message = "Price must be non-negative")
    private Double price;
    @Min(value = 0,message = "Price must be non-negative")
    private Long quantity;
    List<Long> imagesId;
    List<Long> categoryIds;
}