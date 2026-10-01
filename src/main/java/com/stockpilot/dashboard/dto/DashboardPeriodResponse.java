package com.stockpilot.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class DashboardPeriodResponse {

    private Instant from;
    private Instant to;
    private String timezone;
}