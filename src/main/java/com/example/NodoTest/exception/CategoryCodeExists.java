package com.example.NodoTest.exception;

import lombok.Data;

@Data
public class CategoryCodeExists extends RuntimeException {
    public CategoryCodeExists() {
        super("response.category.exists");
    }
}
