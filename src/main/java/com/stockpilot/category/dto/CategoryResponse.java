package com.stockpilot.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}