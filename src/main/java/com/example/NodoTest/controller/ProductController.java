package com.example.NodoTest.controller;

import com.example.NodoTest.dto.ApiResponse;
import com.example.NodoTest.dto.request.ProductRequest;
import com.example.NodoTest.dto.request.ProductUpdate;
import com.example.NodoTest.dto.response.ProductResponse;
import com.example.NodoTest.service.ProductService;
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
@RequestMapping("/api/products")
public class ProductController {

    ProductService productService;
    MessageSource messageSource;

    public ProductController(ProductService productService, MessageSource messageSource) {
        this.productService = productService;
        this.messageSource = messageSource;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> createProduct(
            @Valid ProductRequest productRequest,
            @RequestPart("images") List<MultipartFile> images) throws IOException {

        Locale locale = LocaleContextHolder.getLocale();
        ProductResponse productResponse = productService.createProduct(productRequest, images);

        return ApiResponse.<Long>builder()
                .status(HttpStatus.OK.value())
                .message(messageSource.getMessage("response.success", null, locale))
                .data(productResponse.getId())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id,
                                                      @Valid ProductUpdate productUpdate,
                                                      @RequestParam List<MultipartFile> images
    ) throws IOException {

        Locale locale = LocaleContextHolder.getLocale();
        return ApiResponse.<ProductResponse>builder()
                .status(HttpStatus.OK.value())
                .message(messageSource.getMessage("response.update.success", null, locale))
                .data(productService.updateProduct(id, productUpdate, images))
                .build();
    }
    @GetMapping("/search")
    public ApiResponse<Page<ProductResponse>> searchProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) Date createdFrom,
            @RequestParam(required = false) Date createdTo,
            @RequestParam(required = false) List<Long> categoryIds

    ) {
        Locale locale = LocaleContextHolder.getLocale();
        Page<ProductResponse> products = productService.search(page, size, name, productCode, createdFrom, createdTo, categoryIds);
        return ApiResponse.<Page<ProductResponse>>builder()
                .status(HttpStatus.OK.value())
                .message(messageSource.getMessage("response.success", null, locale))
                .data(products)
                .build();
    }
    @GetMapping("/export")
    public void exportProducts(@RequestParam(required = false) String name,
                               @RequestParam(required = false)  String productCode,
                               @RequestParam(required = false)  Date createdFrom,
                               @RequestParam(required = false)  Date createdTo,
                               @RequestParam(required = false) List<Long> categoryIds){
        productService.export(name, productCode, createdFrom, createdTo,categoryIds);
    }
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
