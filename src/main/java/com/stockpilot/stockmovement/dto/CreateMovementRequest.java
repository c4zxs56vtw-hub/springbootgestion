package com.stockpilot.stockmovement.dto;

import com.stockpilot.stockmovement.model.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateMovementRequest {

    @NotNull(message = "Le produit est obligatoire")
    private Long productId;

    @NotNull(message = "Le type de mouvement est obligatoire")
    private MovementType type;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être supérieure ou égale à 1")
    private Integer quantity;

    private Long supplierId;

    @Size(max = 100, message = "La référence ne doit pas dépasser 100 caractères")
    private String reference;

    @Size(max = 500, message = "La note ne doit pas dépasser 500 caractères")
    private String note;
}