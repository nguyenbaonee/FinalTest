package com.example.NodoTest.controller;

import com.example.NodoTest.dto.ApiResponse;
import com.example.NodoTest.dto.request.CateUpdate;
import com.example.NodoTest.dto.request.CategoryCreate;
import com.example.NodoTest.dto.response.CategoryResponse;
import com.example.NodoTest.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    CategoryService categoryService;
    MessageSource messageSource;

    public CategoryController(CategoryService categoryService, MessageSource messageSource) {
        this.categoryService = categoryService;
        this.messageSource = messageSource;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> createCategory(
            @Valid @ModelAttribute CategoryCreate categoryCreate,
            @RequestParam("images") List<MultipartFile> images) throws IOException {

        Locale locale = LocaleContextHolder.getLocale();
        CategoryResponse cateResponse = categoryService.createCategory(categoryCreate, images);
        return ApiResponse.<Long>builder()
                .status(HttpStatus.OK.value())
            .message(messageSource.getMessage("response.success", null, locale))
                .data(cateResponse.getId())
                .build();
    }


    @GetMapping("/search")
    public ApiResponse<Page<CategoryResponse>> search(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false) String name,
                                                      @RequestParam(required = false, name = "category_code") String categoryCode,
                                                      @RequestParam(required = false) Date createdFrom,
                                                      @RequestParam(required = false) Date createdTo){
        return ApiResponse.<Page<CategoryResponse>>builder()
                .data(categoryService.search(page, size, name, categoryCode, createdFrom, createdTo))
                .build();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid CateUpdate cateUpdate,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        return categoryService.update(id, cateUpdate, images);
    }

    @GetMapping("/export")
    public void export(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String categoryCode,
                       @RequestParam(required = false) Date createdFrom,
                       @RequestParam(required = false) Date createdTo){
        categoryService.export(name, categoryCode, createdFrom, createdTo);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
