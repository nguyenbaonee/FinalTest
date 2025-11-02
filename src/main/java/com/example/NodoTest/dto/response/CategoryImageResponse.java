package com.example.NodoTest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryImageResponse {
    private String url;
    private String fileName;
    private Long categoryId;
}