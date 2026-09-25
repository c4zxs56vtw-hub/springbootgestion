package com.stockpilot.product.dto;

import com.stockpilot.product.model.ProductStatus;
import com.stockpilot.product.model.StockStatus;
import com.stockpilot.product.model.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String sku;
    private String name;
    private String description;

    private CategoryRefResponse category;
    private SupplierRefResponse supplier;

    private Unit unit;

    private String purchasePrice;
    private String salePrice;

    private Integer quantityInStock;
    private Integer minimumStock;

    private ProductStatus status;
    private StockStatus stockStatus;

    private Long stockVersion;

    private Instant createdAt;
    private Instant updatedAt;
}