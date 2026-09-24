package com.stockpilot.category.repository;

import com.stockpilot.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository
        extends JpaRepository<Category, Long> {
}