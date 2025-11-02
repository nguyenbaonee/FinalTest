package com.example.NodoTest.dto.response;

import com.example.NodoTest.model.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    Long id;
    private String name;
    private String productCode;
    private Double price;
    private Long quantity;
    Date createdDate;
    Date modifiedDate;
    String categories;
    List<ProductImage> images;
}
