package com.stockpilot.stockmovement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StockMovementPageResponse {

    private List<StockMovementResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}