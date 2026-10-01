package com.stockpilot.inventory.controller;

import com.stockpilot.inventory.dto.CreateAdjustmentRequest;
import com.stockpilot.inventory.service.InventoryService;
import com.stockpilot.stockmovement.dto.StockMovementResponse;
import com.stockpilot.stockmovement.model.StockMovement;
import com.stockpilot.stockmovement.service.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final StockMovementService stockMovementService;

    public InventoryController(
            InventoryService inventoryService,
            StockMovementService stockMovementService
    ) {
        this.inventoryService = inventoryService;
        this.stockMovementService = stockMovementService;
    }

    @PostMapping("/adjustments")
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementResponse adjust(
            @RequestHeader("Idempotency-Key") UUID idempotencyKey,
            @Valid @RequestBody CreateAdjustmentRequest request
    ) {

        StockMovement movement =
                inventoryService.adjust(
                        idempotencyKey,
                        request
                );

        return stockMovementService.toResponse(movement);
    }
}