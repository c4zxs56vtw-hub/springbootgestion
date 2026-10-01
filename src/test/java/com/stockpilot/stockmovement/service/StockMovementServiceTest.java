package com.stockpilot.stockmovement.service;

import com.stockpilot.common.exception.InsufficientStockException;
import com.stockpilot.common.exception.ProductArchivedException;
import com.stockpilot.common.exception.ResourceNotFoundException;
import com.stockpilot.common.idempotency.service.IdempotencyService;
import com.stockpilot.product.model.Product;
import com.stockpilot.product.model.ProductStatus;
import com.stockpilot.product.model.StockStatus;
import com.stockpilot.product.repository.ProductRepository;
import com.stockpilot.stockmovement.dto.CreateMovementRequest;
import com.stockpilot.stockmovement.model.MovementType;
import com.stockpilot.stockmovement.model.StockMovement;
import com.stockpilot.stockmovement.repository.StockMovementRepository;
import com.stockpilot.supplier.model.Supplier;
import com.stockpilot.supplier.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockMovementServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private IdempotencyService idempotencyService;

    @InjectMocks
    private StockMovementService stockMovementService;

    private UUID idempotencyKey;
    private CreateMovementRequest request;
    private Product product;

    @BeforeEach
    void setUp() {
        idempotencyKey = UUID.randomUUID();
        request = mock(CreateMovementRequest.class);
        lenient().when(request.getSupplierId()).thenReturn(null);
        product = new Product();
        product.setId(1L);
        product.setQuantityInStock(10);
        product.setMinimumStock(5);
        product.setStatus(ProductStatus.ACTIVE);
        product.setStockVersion(1L);
    }

    @Test
    void create_WhenMovementExists_ShouldReturnExisting() {
        when(idempotencyService.hash(anyString())).thenReturn("hash");
        when(idempotencyService.findExisting(eq(idempotencyKey), eq("STOCK_MOVEMENT_CREATE"), eq("hash")))
                .thenReturn(100L);
        
        StockMovement existingMovement = new StockMovement();
        when(stockMovementRepository.findById(100L)).thenReturn(Optional.of(existingMovement));

        StockMovement result = stockMovementService.create(idempotencyKey, request);

        assertEquals(existingMovement, result);
        verify(stockMovementRepository, never()).save(any());
    }

    @Test
    void create_WhenProductNotFound_ShouldThrowException() {
        when(request.getProductId()).thenReturn(1L);
        when(idempotencyService.hash(anyString())).thenReturn("hash");
        when(idempotencyService.findExisting(any(), any(), any())).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            stockMovementService.create(idempotencyKey, request));
    }

    @Test
    void create_WhenProductArchived_ShouldThrowException() {
        product.setStatus(ProductStatus.ARCHIVED);
        when(request.getProductId()).thenReturn(1L);
        when(idempotencyService.hash(anyString())).thenReturn("hash");
        when(idempotencyService.findExisting(any(), any(), any())).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(ProductArchivedException.class, () -> 
            stockMovementService.create(idempotencyKey, request));
    }

    @Test
    void create_WhenTypeIsAdjustment_ShouldThrowException() {
        when(request.getProductId()).thenReturn(1L);
        when(request.getType()).thenReturn(MovementType.ADJUSTMENT);
        when(idempotencyService.hash(anyString())).thenReturn("hash");
        when(idempotencyService.findExisting(any(), any(), any())).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> 
            stockMovementService.create(idempotencyKey, request));
    }

    @Test
    void create_WhenTypeIsOutAndInsufficientStock_ShouldThrowException() {
        when(request.getProductId()).thenReturn(1L);
        when(request.getType()).thenReturn(MovementType.OUT);
        when(request.getQuantity()).thenReturn(15); // more than available 10
        
        when(idempotencyService.hash(anyString())).thenReturn("hash");
        when(idempotencyService.findExisting(any(), any(), any())).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> 
            stockMovementService.create(idempotencyKey, request));
    }

    @Test
    void create_WhenValidInMovement_ShouldUpdateStockAndSave() {
        when(request.getProductId()).thenReturn(1L);
        when(request.getType()).thenReturn(MovementType.IN);
        when(request.getQuantity()).thenReturn(5);
        
        when(idempotencyService.hash(anyString())).thenReturn("hash");
        when(idempotencyService.findExisting(any(), any(), any())).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        StockMovement savedMovement = new StockMovement();
        when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(savedMovement);

        StockMovement result = stockMovementService.create(idempotencyKey, request);

        assertEquals(15, product.getQuantityInStock());
        assertEquals(StockStatus.IN_STOCK, product.getStockStatus());
        assertEquals(2L, product.getStockVersion());
        verify(productRepository).save(product);
        verify(stockMovementRepository).save(any(StockMovement.class));
        assertEquals(savedMovement, result);
    }
    
    @Test
    void create_WhenValidOutMovement_ShouldUpdateStockAndSave() {
        when(request.getProductId()).thenReturn(1L);
        when(request.getType()).thenReturn(MovementType.OUT);
        when(request.getQuantity()).thenReturn(6);
        
        when(idempotencyService.hash(anyString())).thenReturn("hash");
        when(idempotencyService.findExisting(any(), any(), any())).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        StockMovement savedMovement = new StockMovement();
        when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(savedMovement);

        StockMovement result = stockMovementService.create(idempotencyKey, request);

        assertEquals(4, product.getQuantityInStock()); // 10 - 6 = 4 (less than minimum 5)
        assertEquals(StockStatus.LOW_STOCK, product.getStockStatus());
        assertEquals(2L, product.getStockVersion());
        verify(productRepository).save(product);
        verify(stockMovementRepository).save(any(StockMovement.class));
        assertEquals(savedMovement, result);
    }
}
