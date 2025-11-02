package com.example.NodoTest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryImageRequest {
    private String url;
    private String fileName;
    private String altText;
    private Long categoryId;
}

