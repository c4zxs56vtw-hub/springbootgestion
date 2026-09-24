package com.stockpilot.category.repository;

import com.stockpilot.category.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    List<Category> findAll();

    Optional<Category> findById(Long id);

    void deleteById(Long id);

    boolean existsById(Long id);
}