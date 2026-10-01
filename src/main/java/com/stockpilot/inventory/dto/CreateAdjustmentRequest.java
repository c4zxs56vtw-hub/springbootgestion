package com.stockpilot.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateAdjustmentRequest {

    @NotNull(message = "Le produit est obligatoire")
    private Long productId;

    @NotNull(message = "La quantité comptée est obligatoire")
    @Min(value = 0, message = "La quantité comptée ne peut pas être négative")
    private Integer countedQuantity;

    @NotNull(message = "La version du stock est obligatoire")
    private Long expectedStockVersion;

    @NotBlank(message = "La raison de l'ajustement est obligatoire")
    @Size(
            min = 5,
            max = 500,
            message = "La raison doit contenir entre 5 et 500 caractères"
    )
    private String reason;
}