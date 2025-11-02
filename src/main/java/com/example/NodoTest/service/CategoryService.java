package com.example.NodoTest.service;

import com.example.NodoTest.dto.request.CateUpdate;
import com.example.NodoTest.dto.request.CategoryCreate;
import com.example.NodoTest.dto.response.CategoryExport;
import com.example.NodoTest.dto.response.CategoryResponse;
import com.example.NodoTest.exception.CategoryCodeExists;
import com.example.NodoTest.exception.NotFound;
import com.example.NodoTest.mapper.CategoryMapper;
import com.example.NodoTest.model.Category;
import com.example.NodoTest.model.CategoryImage;
import com.example.NodoTest.repo.CategoryImageRepo;
import com.example.NodoTest.repo.CategoryRepo;
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
public class CategoryService {

    CategoryRepo categoryRepo;
    CategoryMapper categoryMapper;
    FileStorageService fileStorageService;
    CategoryImageRepo categoryImageRepo;
    CategoryImageRepo categoryImageRepository;
    public CategoryService(CategoryRepo categoryRepo, CategoryMapper categoryMapper, FileStorageService fileStorageService,
                           CategoryImageRepo categoryImageRepo,
                           CategoryImageRepo categoryImageRepository) {
        this.categoryRepo = categoryRepo;
        this.categoryMapper = categoryMapper;
        this.fileStorageService = fileStorageService;
        this.categoryImageRepo = categoryImageRepo;
        this.categoryImageRepository = categoryImageRepository;
    }

    @Transactional
    public CategoryResponse createCategory(
            CategoryCreate categoryCreate,
            List<MultipartFile> images
    ) throws IOException {

        if (categoryRepo.existsByCategoryCode(categoryCreate.getCategoryCode())) {
            throw new CategoryCodeExists();
        }

        Category category = categoryMapper.toCategory1(categoryCreate);

        List<CategoryImage> cateImages = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                String url = fileStorageService.save(file);

                CategoryImage img = new CategoryImage();
                img.setUrl(url);
                img.setStatus("1");
                img.setCategory(category);
                cateImages.add(img);
            }
        }

        category.setImages(cateImages);

        Category saved = categoryRepo.save(category);
        return categoryMapper.toCategoryResponse(saved);
    }

//    public Page<CategoryResponse> search(int page, int size, String name, String categoryCode, Date createdFrom, Date createdTo) {
//        if (name != null && !name.isBlank()) {
//            name = name.replace("\\", "\\\\")
//                    .replace("%", "\\%")
//                    .replace("_", "\\_")
//                    .toLowerCase();
//            name = "%" + name.toLowerCase() + "%";
//        }
//        else{
//            name = null;
//        }
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        Page<Long> idPage = categoryRepo.searchIds( pageable, name, categoryCode, createdFrom, createdTo);
//        List<Long> ids = idPage.getContent();
//        List<CategoryImage> imagesId = categoryImageRepo.findByCategory_IdIn(ids);
//        List<Category> categories = categoryRepo.findAllById(ids);
//        categories.forEach(category -> {
//            category.setImagesId(imagesId.stream()
//                    .filter(image -> image.getCategory().getId().equals(category.getId()))
//                    .collect(Collectors.toList()));
//        });
//        return new PageImpl<>(categoryMapper.toResponseList(categories),pageable,idPage.getTotalElements());
//    }

    public Page<CategoryResponse> search(int page, int size,
                                         String name,
                                         String categoryCode,
                                         Date createdFrom,
                                         Date createdTo) {
        // Xử lý name cho LIKE
        if (name != null && !name.isBlank()) {
            name = "%" + name.replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_")
                    .toLowerCase() + "%";
        } else {
            name = null;
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Long> idPage = categoryRepo.searchIds(pageable, name, categoryCode, createdFrom, createdTo);
        List<Long> ids = idPage.getContent();
        if (ids.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<Category> categories = categoryRepo.findAllByIdWithImages(ids);

        Map<Long, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        List<Category> categorieResponse = ids.stream()
                .map(categoryMap::get)
                .toList();
        List<CategoryResponse> responseList = categoryMapper.toResponseList(categorieResponse);
        return new PageImpl<>(responseList, pageable, idPage.getTotalElements());
    }

    @Transactional
    public CategoryResponse update(Long id, CateUpdate cateUpdate, List<MultipartFile> images) throws IOException {
        Category category = categoryRepo.findById(id)
                .orElseThrow(NotFound::new);
        if(categoryRepo.existsByCategoryCode(cateUpdate.getCategoryCode())){
            throw new CategoryCodeExists();
        }
        if(cateUpdate.getImagesId() != null && !cateUpdate.getImagesId().isEmpty()){
            categoryImageRepository.deleteCategory(cateUpdate.getImagesId(),id);
        }
        categoryMapper.updateCategory(cateUpdate, category);
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                String url = fileStorageService.save(file);

                CategoryImage img = new CategoryImage();
                img.setUrl(url);
                img.setStatus("1");
                img.setCategory(category);
                categoryImageRepo.save(img);
            }
        }
        Category updated = categoryRepo.save(category);

        return categoryMapper.toCategoryResponse(updated);
    }

public void export(String name, String categoryCode, Date createdFrom, Date createdTo) {
        int page = 0;
        int size = Integer.MAX_VALUE;

    Page<CategoryResponse> pageCateRespon = this.search( page, size,name, categoryCode, createdFrom, createdTo);
    List<CategoryResponse> categories = pageCateRespon.getContent();
    Map<String, Object> data = new HashMap<>();
    data.put("categories", categories);

    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String outputFileName = "output_category_" + timestamp + ".xlsx";

    try (InputStream inputStream = new ClassPathResource("template/search1_template.xlsx").getInputStream()) {

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
    public void deleteCategory(Long id) {
        Category category = categoryRepo.findByIdAndStatus(id, "1")
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setStatus("0");
        category.setModifiedDate(Date.from(Instant.now()));
        categoryRepo.save(category);
    }
}
