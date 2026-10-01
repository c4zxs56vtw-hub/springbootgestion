package com.stockpilot.stockmovement.repository;

import com.stockpilot.stockmovement.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementJpaRepository
        extends JpaRepository<StockMovement, Long> {
}