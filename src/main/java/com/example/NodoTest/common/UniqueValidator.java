package com.example.NodoTest.common;

import com.example.NodoTest.model.Category;
import com.example.NodoTest.model.Product;
import com.example.NodoTest.repo.CategoryRepo;
import com.example.NodoTest.repo.ProductRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueValidator implements ConstraintValidator<UniqueCustom, String> {

    private final ProductRepo productRepository;
    private final CategoryRepo categoryRepository;
    private Class<?> entityClass;
    private String fieldName;

    @Autowired
    public UniqueValidator(ProductRepo productRepository,
                           CategoryRepo categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void initialize(UniqueCustom annotation) {
        this.entityClass = annotation.entity();
        this.fieldName = annotation.field();
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        if (!value.matches("^[a-zA-Z0-9]+$")) return false;

        if (entityClass == Product.class) {
            return !productRepository.existsByProductCode(value);
        } else if (entityClass == Category.class) {
            return !categoryRepository.existsByCategoryCode(value);
        } else {
            throw new IllegalArgumentException("Unknown entity for UniqueCustom");
        }
    }
}
