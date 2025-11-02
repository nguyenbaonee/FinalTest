package com.example.NodoTest.dto.request;

import com.example.NodoTest.model.CategoryImage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    String name;

    @Size(max = 50, message = "Category code cannot be longer than 50 characters")
    String  categoryCode;

    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    String description;

    private List<MultipartFile> images;
}
