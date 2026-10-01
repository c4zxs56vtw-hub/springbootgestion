package com.stockpilot.stockmovement.repository;

import com.stockpilot.stockmovement.model.StockMovement;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostgresStockMovementRepository
        implements StockMovementRepository {

    private final StockMovementJpaRepository jpaRepository;

    public PostgresStockMovementRepository(
            StockMovementJpaRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StockMovement save(StockMovement stockMovement) {
        return jpaRepository.save(stockMovement);
    }

    @Override
    public List<StockMovement> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public org.springframework.data.domain.Page<StockMovement> findByProductId(Long productId, org.springframework.data.domain.Pageable pageable) {
        return jpaRepository.findByProductId(productId, pageable);
    }

    @Override
    public Optional<StockMovement> findById(Long id) {
        return jpaRepository.findById(id);
    }
}
