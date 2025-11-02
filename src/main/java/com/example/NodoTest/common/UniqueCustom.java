package com.example.NodoTest.common;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueValidator.class)
public @interface UniqueCustom {
    String message() default "Value must be unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // Thêm 2 thuộc tính này
    Class<?> entity();   // tên entity (Product.class, Category.class)
    String field();      // tên field trong entity (productCode, categoryCode)
}

