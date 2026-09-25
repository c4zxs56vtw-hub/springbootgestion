package com.stockpilot.supplier.repository;

import com.stockpilot.supplier.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierJpaRepository
        extends JpaRepository<Supplier, Long> {
}