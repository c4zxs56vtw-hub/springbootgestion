package com.stockpilot.stockmovement.repository;

import com.stockpilot.stockmovement.model.StockMovement;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockMovementRepository {

    StockMovement save(StockMovement stockMovement);

    List<StockMovement> findAll();

    Page<StockMovement> findByProductId(Long productId, Pageable pageable);

    Optional<StockMovement> findById(Long id);
}