package com.example.NodoTest.dto.response;

import com.example.NodoTest.model.CategoryImage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;

    private String name;

    private String categoryCode;

    private String description;

    private List<CategoryImageResponse> images;

    private Date createdDate;

    private Date modifiedDate;

    private String status;
}
