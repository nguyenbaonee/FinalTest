package com.example.NodoTest.dto.request;

import com.example.NodoTest.common.UniqueCustom;
import com.example.NodoTest.model.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreate {
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    String name;

    @Size(max = 50, message = "Category code cannot be longer than 50 characters")
    @UniqueCustom(entity = Category.class, field = "categoryCode")
    @JsonProperty("category_code")
    String  categoryCode;

    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    String description;

}