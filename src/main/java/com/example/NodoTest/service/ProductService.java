package com.example.NodoTest.service;

import com.example.NodoTest.dto.request.ProductRequest;
import com.example.NodoTest.dto.request.ProductSearch;
import com.example.NodoTest.dto.request.ProductUpdate;
import com.example.NodoTest.dto.response.CategoryResponse;
import com.example.NodoTest.dto.response.ProductResponse;
import com.example.NodoTest.exception.AlreadyExists;
import com.example.NodoTest.exception.NotFound;
import com.example.NodoTest.repo.ProductCategoryRepo;
import com.example.NodoTest.repo.ProductImageRepo;
import com.example.NodoTest.mapper.ProductMapper;
import com.example.NodoTest.model.*;
import com.example.NodoTest.repo.CategoryRepo;
import com.example.NodoTest.repo.ProductRepo;
import org.jxls.builder.JxlsOutput;
import org.jxls.builder.JxlsOutputFile;
import org.jxls.transform.poi.JxlsPoiTemplateFillerBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    ProductRepo productRepo;
    ProductMapper productMapper;
    FileStorageService fileStorageService;
    ProductImageRepo productImageRepo;
    CategoryRepo categoryRepo;
    ProductCategoryRepo productCategoryRepo;

    public ProductService(ProductRepo productRepo, ProductMapper productMapper, FileStorageService fileStorageService,
                          ProductImageRepo productImageRepo, CategoryRepo categoryRepo, ProductCategoryRepo productCategoryRepo) {
        this.productRepo = productRepo;
        this.productMapper = productMapper;
        this.fileStorageService = fileStorageService;
        this.productImageRepo = productImageRepo;
        this.categoryRepo = categoryRepo;
        this.productCategoryRepo = productCategoryRepo;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest, List<MultipartFile> images) throws IOException {
        if(productRepo.existsByProductCode(productRequest.getProductCode())) {
            throw new AlreadyExists();
        }
        List<Long> categoryIds = productRequest.getCategoryIds();
        List<Category> categories = categoryRepo.findAllById(categoryIds);

        if (categories.size() != categoryIds.size()) {
            throw new NotFound();
        }

        Product product = productMapper.toProduct(productRequest);

        List<ProductCategory> links = categories.stream()
                .map(category -> {
                    ProductCategory pc = new ProductCategory();
                    pc.setProduct(product);
                    pc.setCategory(category);
                    return pc;
                })
                .toList();

        product.setProductCategories(links);

        List<ProductImage> productImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                String url = fileStorageService.save(file);
                ProductImage img = new ProductImage();
                img.setUrl(url);
                img.setStatus("1");
                img.setProduct(product);
                productImages.add(img);
            }
        }
        product.setImages(productImages);
        Product saved = productRepo.save(product);
        return productMapper.toProductResponse(saved);
    }

    public Page<ProductResponse> search(int page, int size,
                                         String name,
                                         String productCode,
                                         Date createdFrom,
                                         Date createdTo,
                                        List<Long> categoryIds) {
        if (name != null && !name.isBlank()) {
            name = "%" + name.replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_")
                    .toLowerCase() + "%";
        } else {
            name = null;
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Long> idPages = productRepo.searchIds(pageable, name, productCode, createdFrom, createdTo,categoryIds);
        List<Long> idPage = idPages.getContent();
        if (idPage.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

//        List<Product> products = productRepo.findAllById(idPage);
//
//        List<ProductImage> productImages = productImageRepo.findByProductIdIn(idPage);
//
//        Map<Long, List<ProductImage>> imagesMap = productImages.stream()
//                .collect(Collectors.groupingBy(img -> img.getProduct().getId()));
//
//        for (Product product : products) {
//            List<ProductImage> images = imagesMap.getOrDefault(product.getId(), Collections.emptyList());
//            product.setImages(images);
//        }

        List<Product> products = productRepo.findAllWithImagesAndCategories(idPage);

        List<ProductResponse> responseList = products.stream().map(product -> {
            ProductResponse dto = productMapper.toProductResponse(product);

            String categories = product.getProductCategories().stream()
                    .filter(pc -> "1".equals(pc.getStatus())) // chỉ lấy active
                    .map(pc -> pc.getCategory().getName())
                    .collect(Collectors.joining(", "));

            dto.setCategories(categories);
            return dto;
        }).toList();

        return new PageImpl<>(responseList, pageable, idPages.getTotalElements());

    }

    public void export(String name,
                       String productCode,
                       Date createdFrom,
                       Date createdTo,
                       List<Long> categoryIds) {
        int page = 0;
        int size = Integer.MAX_VALUE;

        Page<ProductResponse> pageRespon = this.search( page, size, name, productCode, createdFrom, createdTo,categoryIds);
        List<ProductResponse> productResponse = pageRespon.getContent();
        Map<String, Object> data = new HashMap<>();
        data.put("productResponse", productResponse);

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String outputFileName = "output_product_" + timestamp + ".xlsx";

        try (InputStream inputStream = new ClassPathResource("template/imput_product.xlsx").getInputStream()) {

            File exportDir = new File("./exports"); // thư mục ngoài project
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File outputFile = new File(exportDir, outputFileName);

            JxlsOutput output = new JxlsOutputFile(outputFile);
            JxlsPoiTemplateFillerBuilder.newInstance()
                    .withTemplate(inputStream)
                    .build()
                    .fill(data, output);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export category data", e);
        }
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdate productUpdate, List<MultipartFile> images) throws IOException {
        Product product = productRepo.findByIdAndStatus(productId, "1")
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productMapper.updateProduct1(productUpdate, product);
        List<ProductCategory> oldCategories = productCategoryRepo.findByProduct_Id(product.getId());
        Set<Long> oldCategoryIds = oldCategories.stream()
                .filter(pc -> pc.getStatus().equals("1"))
                .map(pc -> pc.getCategory().getId())
                .collect(Collectors.toSet());

        Set<Long> newCategoryIds = new HashSet<>(productUpdate.getCategoryIds());
        oldCategories.forEach(pc -> {
            if (!newCategoryIds.contains(pc.getCategory().getId()) && Objects.equals(pc.getStatus(), "1")) {
                pc.setStatus("0");
                pc.setModifiedDate(Date.from(Instant.now()));
            }
        });

        List<ProductImage> oldImages = productImageRepo.findByProduct_IdAndStatus(product.getId(), "1");

        Set<Long> deleteIds = productUpdate.getImagesId() != null ? new HashSet<>(productUpdate.getImagesId()) : new HashSet<>();

        // Soft delete những ảnh có trong list xóa
        oldImages.forEach(img -> {
            if (deleteIds.contains(img.getId())) {
                img.setStatus("0");
                img.setModifiedDate(Date.from(Instant.now()));
                productImageRepo.save(img);
            }
        });

        if (images != null && !images.isEmpty()) {
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : images) {
                String url = fileStorageService.save(file);
                ProductImage img = new ProductImage();
                img.setUrl(url);
                img.setStatus("1");
                img.setProduct(product);
                productImages.add(img);
            }
            product.getImages().addAll(productImages);
        }

        Product updated = productRepo.save(product);
        return productMapper.toProductResponse(updated);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepo.findByIdAndStatus(productId, "1")
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus("0");
        productRepo.save(product);
    }

}
