package com.stockpilot.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SupplierPageResponse {

    private List<SupplierResponse> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;
}