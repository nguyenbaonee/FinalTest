package com.example.NodoTest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearch {
    private String name;
    private String productCode;
    private String description;
    Date createdFrom;
    Date createdTo;
    List<Long> categoryIds;
}