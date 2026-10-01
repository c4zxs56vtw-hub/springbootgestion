package com.stockpilot.stockmovement.service;
import com.stockpilot.stockmovement.dto.StockMovementPageResponse;

import java.util.Comparator;
import java.util.List;
import com.stockpilot.common.exception.InsufficientStockException;
import com.stockpilot.common.exception.ProductArchivedException;
import com.stockpilot.common.exception.ResourceNotFoundException;
import com.stockpilot.product.model.Product;
import com.stockpilot.product.model.ProductStatus;
import com.stockpilot.product.model.StockStatus;
import com.stockpilot.product.repository.ProductRepository;
import com.stockpilot.stockmovement.dto.CreateMovementRequest;
import com.stockpilot.stockmovement.dto.ProductRefResponse;
import com.stockpilot.stockmovement.dto.StockMovementResponse;
import com.stockpilot.stockmovement.dto.SupplierRefResponse;
import com.stockpilot.stockmovement.model.MovementType;
import com.stockpilot.stockmovement.model.StockMovement;
import com.stockpilot.stockmovement.repository.StockMovementRepository;
import com.stockpilot.supplier.model.Supplier;
import com.stockpilot.supplier.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public StockMovementService(
            StockMovementRepository stockMovementRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository
    ) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public StockMovement create(CreateMovementRequest request) {

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Produit introuvable avec l'id : "
                                        + request.getProductId()
                        )
                );

        if (product.getStatus() == ProductStatus.ARCHIVED) {
            throw new ProductArchivedException(
                    "Impossible de modifier le stock d'un produit archivé"
            );
        }

        if (request.getType() == MovementType.ADJUSTMENT) {
            throw new IllegalArgumentException(
                    "ADJUSTMENT ne peut pas être créé depuis cette opération"
            );
        }

        Supplier supplier = null;

        if (request.getSupplierId() != null) {
            supplier = supplierRepository
                    .findById(request.getSupplierId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Fournisseur introuvable avec l'id : "
                                            + request.getSupplierId()
                            )
                    );
        }

        int quantityBefore = product.getQuantityInStock();

        int quantityDelta;
        int quantityAfter;

        if (request.getType() == MovementType.IN) {

            quantityDelta = request.getQuantity();
            quantityAfter = quantityBefore + request.getQuantity();

        } else {

            if (request.getQuantity() > quantityBefore) {
                throw new InsufficientStockException(
                        "Stock insuffisant. Stock disponible : "
                                + quantityBefore
                );
            }

            quantityDelta = -request.getQuantity();
            quantityAfter = quantityBefore - request.getQuantity();
        }

        product.setQuantityInStock(quantityAfter);

        product.setStockStatus(
                calculateStockStatus(
                        quantityAfter,
                        product.getMinimumStock()
                )
        );

        product.setStockVersion(
                product.getStockVersion() + 1
        );

        product.setUpdatedAt(Instant.now());

        productRepository.save(product);

        StockMovement movement = new StockMovement(
                null,
                product,
                request.getType(),
                quantityBefore,
                quantityDelta,
                quantityAfter,
                supplier,
                clean(request.getReference()),
                clean(request.getNote()),
                Instant.now()
        );

        return stockMovementRepository.save(movement);
    }

    public StockMovement findById(Long id) {
        return stockMovementRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Mouvement de stock introuvable avec l'id : " + id
                        )
                );
    }

    public StockMovementResponse toResponse(
            StockMovement movement
    ) {

        ProductRefResponse product =
                new ProductRefResponse(
                        movement.getProduct().getId(),
                        movement.getProduct().getSku(),
                        movement.getProduct().getName(),
                        movement.getProduct().getUnit()
                );

        SupplierRefResponse supplier = null;

        if (movement.getSupplier() != null) {
            supplier = new SupplierRefResponse(
                    movement.getSupplier().getId(),
                    movement.getSupplier().getName()
            );
        }

        return new StockMovementResponse(
                movement.getId(),
                product,
                movement.getType(),
                movement.getQuantityBefore(),
                movement.getQuantityDelta(),
                movement.getQuantityAfter(),
                supplier,
                movement.getReference(),
                movement.getNote(),
                movement.getCreatedAt()
        );
    }

    private StockStatus calculateStockStatus(
            int quantity,
            int minimumStock
    ) {

        if (quantity == 0) {
            return StockStatus.OUT_OF_STOCK;
        }

        if (quantity <= minimumStock) {
            return StockStatus.LOW_STOCK;
        }

        return StockStatus.IN_STOCK;
    }

    private String clean(String value) {
        return value == null
                ? null
                : value.trim();
    }

    public StockMovementPageResponse findAll(
            int page,
            int size,
            String sort,
            Long productId,
            MovementType type,
            Instant from,
            Instant to
    ) {

        List<StockMovement> movements =
                stockMovementRepository.findAll();

        if (productId != null) {
            movements = movements.stream()
                    .filter(movement ->
                            movement.getProduct() != null
                                    && movement.getProduct()
                                    .getId()
                                    .equals(productId)
                    )
                    .toList();
        }

        if (type != null) {
            movements = movements.stream()
                    .filter(movement ->
                            movement.getType() == type
                    )
                    .toList();
        }

        if (from != null) {
            movements = movements.stream()
                    .filter(movement ->
                            !movement.getCreatedAt().isBefore(from)
                    )
                    .toList();
        }

        if (to != null) {
            movements = movements.stream()
                    .filter(movement ->
                            !movement.getCreatedAt().isAfter(to)
                    )
                    .toList();
        }

        Comparator<StockMovement> comparator =
                Comparator.comparing(
                        StockMovement::getCreatedAt
                );

        if ("createdAt,desc".equalsIgnoreCase(sort)) {
            comparator = comparator.reversed();
        }

        movements = movements.stream()
                .sorted(comparator)
                .toList();

        long totalElements = movements.size();

        int totalPages =
                (int) Math.ceil(
                        (double) totalElements / size
                );

        int fromIndex = page * size;

        if (fromIndex >= totalElements) {
            return new StockMovementPageResponse(
                    List.of(),
                    page,
                    size,
                    totalElements,
                    totalPages
            );
        }

        int toIndex =
                Math.min(
                        fromIndex + size,
                        movements.size()
                );

        List<StockMovementResponse> content =
                movements
                        .subList(fromIndex, toIndex)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return new StockMovementPageResponse(
                content,
                page,
                size,
                totalElements,
                totalPages
        );
    }
}