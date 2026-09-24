package com.stockpilot.category.controller;

import com.stockpilot.category.dto.CategoryPageResponse;
import com.stockpilot.category.dto.CategoryResponse;
import com.stockpilot.category.dto.CreateCategoryRequest;
import com.stockpilot.category.model.Category;
import com.stockpilot.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(
            @Valid @RequestBody CreateCategoryRequest request
    ) {

        Category category = categoryService.create(
                request.getName(),
                request.getDescription()
        );

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    @GetMapping
    public CategoryPageResponse findAll(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ) {

        return categoryService.findAll(
                q,
                page,
                size,
                sort
        );
    }
}