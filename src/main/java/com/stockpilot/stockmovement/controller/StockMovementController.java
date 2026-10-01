package com.stockpilot.stockmovement.controller;

import com.stockpilot.stockmovement.dto.CreateMovementRequest;
import com.stockpilot.stockmovement.dto.StockMovementResponse;
import com.stockpilot.stockmovement.model.StockMovement;
import com.stockpilot.stockmovement.service.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.stockpilot.stockmovement.dto.StockMovementPageResponse;
import com.stockpilot.stockmovement.model.MovementType;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.UUID;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(
            StockMovementService stockMovementService
    ) {
        this.stockMovementService = stockMovementService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementResponse create(
            @RequestHeader("Idempotency-Key") UUID idempotencyKey,
            @Valid @RequestBody CreateMovementRequest request
    ) {
        StockMovement movement =
                stockMovementService.create(
                        idempotencyKey,
                        request
                );

        return stockMovementService.toResponse(movement);
    }

    @GetMapping("/{id}")
    public StockMovementResponse findById(
            @PathVariable Long id
    ) {
        StockMovement movement =
                stockMovementService.findById(id);

        return stockMovementService.toResponse(movement);
    }

    @GetMapping
    public StockMovementPageResponse findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) MovementType type,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to
    ) {
        return stockMovementService.findAll(
                page,
                size,
                sort,
                productId,
                type,
                from,
                to
        );
    }
}