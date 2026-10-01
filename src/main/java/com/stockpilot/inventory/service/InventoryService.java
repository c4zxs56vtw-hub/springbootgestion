package com.stockpilot.inventory.service;

import com.stockpilot.common.exception.NoStockChangeException;
import com.stockpilot.common.exception.ProductArchivedException;
import com.stockpilot.common.exception.ResourceNotFoundException;
import com.stockpilot.common.exception.StockVersionConflictException;
import com.stockpilot.common.idempotency.service.IdempotencyService;
import com.stockpilot.inventory.dto.CreateAdjustmentRequest;
import com.stockpilot.product.model.Product;
import com.stockpilot.product.model.ProductStatus;
import com.stockpilot.product.model.StockStatus;
import com.stockpilot.product.repository.ProductRepository;
import com.stockpilot.stockmovement.model.MovementType;
import com.stockpilot.stockmovement.model.StockMovement;
import com.stockpilot.stockmovement.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class InventoryService {

    private final IdempotencyService idempotencyService;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    public InventoryService(
            ProductRepository productRepository,
            StockMovementRepository stockMovementRepository,
            IdempotencyService idempotencyService
    ) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.idempotencyService = idempotencyService;
    }

    @Transactional
    public StockMovement adjust(
            UUID idempotencyKey,
            CreateAdjustmentRequest request
    ) {

        // 1. Construire une représentation de la requête
        String requestData =
                request.getProductId()
                        + "|"
                        + request.getCountedQuantity()
                        + "|"
                        + request.getExpectedStockVersion()
                        + "|"
                        + request.getReason();

        // 2. Générer le hash de la requête
        String requestHash =
                idempotencyService.hash(requestData);

        // 3. Vérifier si cette clé a déjà été utilisée
        Long existingMovementId =
                idempotencyService.findExisting(
                        idempotencyKey,
                        "INVENTORY_ADJUSTMENT",
                        requestHash
                );

        // 4. Si la même requête a déjà été exécutée,
        // retourner le mouvement existant
        if (existingMovementId != null) {

            return stockMovementRepository
                    .findById(existingMovementId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Mouvement d'ajustement introuvable avec l'id : "
                                            + existingMovementId
                            )
                    );
        }

        // 5. Récupérer le produit
        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Produit introuvable avec l'id : "
                                        + request.getProductId()
                        )
                );

        // 6. Empêcher les modifications d'un produit archivé
        if (product.getStatus() == ProductStatus.ARCHIVED) {
            throw new ProductArchivedException(
                    "Impossible d'ajuster le stock d'un produit archivé"
            );
        }

        // 7. Vérifier la version du stock
        if (!product.getStockVersion()
                .equals(request.getExpectedStockVersion())) {

            throw new StockVersionConflictException(
                    "La version du stock a changé. Version actuelle : "
                            + product.getStockVersion()
            );
        }

        int quantityBefore = product.getQuantityInStock();
        int quantityAfter = request.getCountedQuantity();

        int quantityDelta =
                quantityAfter - quantityBefore;

        // 8. Aucun changement
        if (quantityDelta == 0) {
            throw new NoStockChangeException(
                    "La quantité comptée est identique au stock actuel"
            );
        }

        // 9. Mettre à jour le produit
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

        // 10. Créer le mouvement ADJUSTMENT
        StockMovement movement =
                new StockMovement(
                        null,
                        product,
                        MovementType.ADJUSTMENT,
                        quantityBefore,
                        quantityDelta,
                        quantityAfter,
                        null,
                        null,
                        request.getReason().trim(),
                        Instant.now()
                );

        StockMovement savedMovement =
                stockMovementRepository.save(movement);

        // 11. Mémoriser l'Idempotency-Key
        idempotencyService.remember(
                idempotencyKey,
                "INVENTORY_ADJUSTMENT",
                requestHash,
                savedMovement.getId()
        );

        return savedMovement;
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
}