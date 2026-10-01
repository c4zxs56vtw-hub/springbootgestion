package com.stockpilot.product.repository;

import com.stockpilot.product.model.Product;
import com.stockpilot.product.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostgresProductRepository
        implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    public PostgresProductRepository(
            ProductJpaRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        return jpaRepository.save(product);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findAll(org.springframework.data.jpa.domain.Specification<Product> spec, Pageable pageable) {
        return jpaRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return jpaRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

    @Override
    public boolean existsByCategoryIdAndStatus(Long categoryId, ProductStatus status) {
        return jpaRepository.existsByCategoryIdAndStatus(categoryId, status);
    }

    @Override
    public boolean existsBySupplierIdAndStatus(Long supplierId, ProductStatus status) {
        return jpaRepository.existsBySupplierIdAndStatus(supplierId, status);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}