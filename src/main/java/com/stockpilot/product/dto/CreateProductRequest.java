package com.stockpilot.product.dto;

import com.stockpilot.product.model.Unit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateProductRequest {

    @NotBlank(message = "Le SKU est obligatoire")
    private String sku;

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String name;

    private String description;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categoryId;

    private Long supplierId;

    @NotNull(message = "L'unité est obligatoire")
    private Unit unit;

    @NotBlank(message = "Le prix d'achat est obligatoire")
    private String purchasePrice;

    @NotBlank(message = "Le prix de vente est obligatoire")
    private String salePrice;

    @NotNull(message = "Le stock minimum est obligatoire")
    @Min(value = 0, message = "Le stock minimum ne peut pas être négatif")
    private Integer minimumStock;
}