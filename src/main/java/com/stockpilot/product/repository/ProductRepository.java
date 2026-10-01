package com.stockpilot.product.repository;

import com.stockpilot.product.model.Product;
import com.stockpilot.product.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByStockStatus(com.stockpilot.product.model.StockStatus stockStatus, Pageable pageable);

    Optional<Product> findById(Long id);

    boolean existsBySku(String sku);

    boolean existsByCategoryIdAndStatus(Long categoryId, ProductStatus status);

    boolean existsBySupplierIdAndStatus(Long supplierId, ProductStatus status);

    void deleteById(Long id);
}