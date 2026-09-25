package com.stockpilot.product.controller;

import com.stockpilot.product.dto.CreateProductRequest;
import com.stockpilot.product.dto.ProductPageResponse;
import com.stockpilot.product.dto.ProductResponse;
import com.stockpilot.product.model.Product;
import com.stockpilot.product.model.ProductStatus;
import com.stockpilot.product.model.StockStatus;
import com.stockpilot.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(
            @Valid @RequestBody CreateProductRequest request
    ) {
        Product product = productService.create(request);

        return productService.toResponse(product);
    }

    @GetMapping
    public ProductPageResponse findAll(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) StockStatus stockStatus
    ) {
        return productService.findAll(
                q,
                page,
                size,
                sort,
                categoryId,
                supplierId,
                status,
                stockStatus
        );
    }

    @GetMapping("/{id}")
    public ProductResponse findById(
            @PathVariable Long id
    ) {
        Product product = productService.findById(id);

        return productService.toResponse(product);
    }

    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest request
    ) {
        Product product = productService.update(id, request);

        return productService.toResponse(product);
    }

    @PatchMapping("/{id}/archive")
    public ProductResponse archive(
            @PathVariable Long id
    ) {
        Product product = productService.archive(id);

        return productService.toResponse(product);
    }
}