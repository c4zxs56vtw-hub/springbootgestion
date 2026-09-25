package com.stockpilot.product.repository;

import com.stockpilot.product.model.Product;
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
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}