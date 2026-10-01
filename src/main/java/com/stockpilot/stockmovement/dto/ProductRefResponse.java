package com.stockpilot.stockmovement.dto;

import com.stockpilot.product.model.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductRefResponse {

    private Long id;
    private String sku;
    private String name;
    private Unit unit;
}