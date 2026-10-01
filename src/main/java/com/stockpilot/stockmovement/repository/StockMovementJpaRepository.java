package com.stockpilot.stockmovement.repository;

import com.stockpilot.stockmovement.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockMovementJpaRepository
        extends JpaRepository<StockMovement, Long> {
    Page<StockMovement> findByProductId(Long productId, Pageable pageable);
}