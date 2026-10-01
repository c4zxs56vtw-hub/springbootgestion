package com.stockpilot.dashboard.dto;

import com.stockpilot.stockmovement.dto.StockMovementResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardResponse {

    private String currency;

    private DashboardPeriodResponse period;

    private DashboardCountsResponse counts;

    private String stockValue;

    private MovementCountsResponse movementCounts;

    private List<DailySeriesResponse> dailySeries;

    private List<StockMovementResponse> recentMovements;
}