package com.stockpilot.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DailySeriesResponse {

    private LocalDate date;
    private long in;
    private long out;
    private long adjustments;
}