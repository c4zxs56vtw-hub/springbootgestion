package com.stockpilot.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardCountsResponse {

    private long activeProducts;
    private long lowStockProducts;
    private long outOfStockProducts;
}