package com.stockpilot.supplier.repository;

import com.stockpilot.supplier.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository {

    Supplier save(Supplier supplier);

    List<Supplier> findAll();

    Optional<Supplier> findById(Long id);

    void deleteById(Long id);

    boolean existsById(Long id);
}