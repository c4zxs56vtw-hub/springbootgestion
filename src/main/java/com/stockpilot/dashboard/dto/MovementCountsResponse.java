package com.stockpilot.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovementCountsResponse {

    private long in;
    private long out;
}