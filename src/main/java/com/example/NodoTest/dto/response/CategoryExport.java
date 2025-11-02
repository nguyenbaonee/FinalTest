package com.example.NodoTest.dto.response;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryExport {
    String id;
    String name;
    String  categoryCode;
    String description;
    Date createDate;
    Date modifiedDate;
    String createdBy;
    String modifiedBy;
}