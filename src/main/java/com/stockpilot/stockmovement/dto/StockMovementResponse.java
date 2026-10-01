package com.stockpilot.stockmovement.dto;

import com.stockpilot.stockmovement.model.MovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class StockMovementResponse {

    private Long id;

    private ProductRefResponse product;

    private MovementType type;

    private Integer quantityBefore;
    private Integer quantityDelta;
    private Integer quantityAfter;

    private SupplierRefResponse supplier;

    private String reference;
    private String note;

    private Instant createdAt;
}