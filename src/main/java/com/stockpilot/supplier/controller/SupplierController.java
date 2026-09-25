package com.stockpilot.supplier.controller;

import com.stockpilot.supplier.dto.CreateSupplierRequest;
import com.stockpilot.supplier.dto.SupplierPageResponse;
import com.stockpilot.supplier.dto.SupplierResponse;
import com.stockpilot.supplier.model.Supplier;
import com.stockpilot.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierResponse create(
            @Valid @RequestBody CreateSupplierRequest request
    ) {
        Supplier supplier = supplierService.create(
                request.getName(),
                request.getContactName(),
                request.getEmail(),
                request.getPhone(),
                request.getAddress()
        );

        return toResponse(supplier);
    }

    @GetMapping
    public SupplierPageResponse findAll(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ) {
        return supplierService.findAll(q, page, size, sort);
    }

    @GetMapping("/{id}")
    public SupplierResponse findById(
            @PathVariable Long id
    ) {
        return toResponse(
                supplierService.findById(id)
        );
    }

    @PutMapping("/{id}")
    public SupplierResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CreateSupplierRequest request
    ) {
        Supplier supplier = supplierService.update(
                id,
                request.getName(),
                request.getContactName(),
                request.getEmail(),
                request.getPhone(),
                request.getAddress()
        );

        return toResponse(supplier);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        supplierService.delete(id);
    }

    private SupplierResponse toResponse(Supplier supplier) {
        return new SupplierResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt()
        );
    }
}