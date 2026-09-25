package com.stockpilot.supplier.repository;

import com.stockpilot.supplier.model.Supplier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostgresSupplierRepository
        implements SupplierRepository {

    private final SupplierJpaRepository jpaRepository;

    public PostgresSupplierRepository(
            SupplierJpaRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Supplier save(Supplier supplier) {
        return jpaRepository.save(supplier);
    }

    @Override
    public List<Supplier> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}