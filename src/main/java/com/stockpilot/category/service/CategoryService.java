package com.stockpilot.category.service;

import com.stockpilot.category.dto.CategoryPageResponse;
import com.stockpilot.category.dto.CategoryResponse;
import com.stockpilot.category.model.Category;
import com.stockpilot.category.repository.CategoryRepository;
import com.stockpilot.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category create(
            String name,
            String description
    ) {

        Instant now = Instant.now();

        Category category = new Category(
                null,
                name.trim(),
                description != null
                        ? description.trim()
                        : null,
                now,
                now
        );

        return categoryRepository.save(category);
    }
    public CategoryPageResponse findAll(
            String q,
            int page,
            int size,
            String sort
    ) {

        List<Category> categories = categoryRepository.findAll();

        if (q != null && !q.isBlank()) {

            String search = q.trim().toLowerCase();

            categories = categories.stream()
                    .filter(category ->
                            category.getName()
                                    .toLowerCase()
                                    .contains(search)
                    )
                    .toList();
        }

        Comparator<Category> comparator =
                Comparator.comparing(
                        Category::getName,
                        String.CASE_INSENSITIVE_ORDER
                );

        if ("name,desc".equalsIgnoreCase(sort)) {
            comparator = comparator.reversed();
        }

        categories = categories.stream()
                .sorted(comparator)
                .toList();

        long totalElements = categories.size();

        int totalPages = (int) Math.ceil(
                (double) totalElements / size
        );

        int start = page * size;

        if (start >= totalElements) {
            return new CategoryPageResponse(
                    List.of(),
                    page,
                    size,
                    totalElements,
                    totalPages
            );
        }

        int end = Math.min(start + size, categories.size());

        List<CategoryResponse> content =
                categories.subList(start, end)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return new CategoryPageResponse(
                content,
                page,
                size,
                totalElements,
                totalPages
        );
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "La catégorie avec l'id " + id + " n'existe pas"
                        )
                );
    }
    public Category update(
            Long id,
            String name,
            String description
    ) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "La catégorie avec l'id " + id + " n'existe pas"
                        )
                );

        category.setName(name.trim());
        category.setDescription(
                description != null ? description.trim() : null
        );
        category.setUpdatedAt(Instant.now());

        return categoryRepository.save(category);
    }
    public void delete(Long id) {

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "La catégorie avec l'id " + id + " n'existe pas"
            );
        }

        categoryRepository.deleteById(id);
    }
}