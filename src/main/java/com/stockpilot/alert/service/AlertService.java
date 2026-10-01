package com.stockpilot.alert.service;

import com.stockpilot.product.dto.ProductPageResponse;
import com.stockpilot.product.dto.ProductResponse;
import com.stockpilot.product.model.Product;
import com.stockpilot.product.model.StockStatus;
import com.stockpilot.product.repository.ProductRepository;
import com.stockpilot.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    private final ProductRepository productRepository;
    private final ProductService productService;

    public AlertService(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    public ProductPageResponse findOutOfStock(int page, int size, String sort) {
        return findByStockStatus(StockStatus.OUT_OF_STOCK, page, size, sort);
    }

    public ProductPageResponse findLowStock(int page, int size, String sort) {
        return findByStockStatus(StockStatus.LOW_STOCK, page, size, sort);
    }

    private ProductPageResponse findByStockStatus(StockStatus status, int page, int size, String sort) {
        Sort.Direction direction = sort.toLowerCase().endsWith(",desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sort.split(",")[0];

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Product> productPage = productRepository.findByStockStatus(status, pageable);

        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(productService::toResponse)
                .toList();

        return new ProductPageResponse(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }
}
