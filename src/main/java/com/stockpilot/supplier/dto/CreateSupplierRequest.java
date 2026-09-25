package com.stockpilot.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateSupplierRequest {

    @NotBlank(message = "Le nom du fournisseur est obligatoire")
    @Size(min = 2, max = 150, message = "Le nom doit contenir entre 2 et 150 caractères")
    private String name;

    private String contactName;

    @Email(message = "L'adresse email est invalide")
    private String email;

    private String phone;

    private String address;
}