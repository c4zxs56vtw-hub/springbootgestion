
package com.stockpilot.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryPageResponse {

    private List<CategoryResponse> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;
}