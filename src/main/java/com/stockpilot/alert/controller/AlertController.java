package com.stockpilot.alert.controller;

import com.stockpilot.alert.service.AlertService;
import com.stockpilot.product.dto.ProductPageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/out-of-stock")
    public ProductPageResponse getOutOfStock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ) {
        return alertService.findOutOfStock(page, size, sort);
    }

    @GetMapping("/low-stock")
    public ProductPageResponse getLowStock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ) {
        return alertService.findLowStock(page, size, sort);
    }
}
