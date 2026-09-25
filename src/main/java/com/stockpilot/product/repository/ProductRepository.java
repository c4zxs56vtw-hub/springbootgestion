package com.stockpilot.product.repository;

import com.stockpilot.product.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    List<Product> findAll();

    Optional<Product> findById(Long id);

    boolean existsBySku(String sku);

    void deleteById(Long id);
}