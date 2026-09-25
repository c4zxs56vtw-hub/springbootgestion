package com.stockpilot.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProblemDetailsResponse {

    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
    private String code;
    private List<FieldErrorResponse> fieldErrors;
}