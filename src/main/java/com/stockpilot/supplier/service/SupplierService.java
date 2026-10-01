package com.stockpilot.supplier.service;

import com.stockpilot.common.exception.ResourceNotFoundException;
import com.stockpilot.common.exception.SupplierInUseException;
import com.stockpilot.product.model.ProductStatus;
import com.stockpilot.product.repository.ProductRepository;
import com.stockpilot.supplier.dto.SupplierPageResponse;
import com.stockpilot.supplier.dto.SupplierResponse;
import com.stockpilot.supplier.model.Supplier;
import com.stockpilot.supplier.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public SupplierService(
            SupplierRepository supplierRepository,
            ProductRepository productRepository
    ) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    public Supplier create(
            String name,
            String contactName,
            String email,
            String phone,
            String address
    ) {

        Instant now = Instant.now();

        Supplier supplier = new Supplier(
                null,
                name.trim(),
                clean(contactName),
                clean(email),
                clean(phone),
                clean(address),
                now,
                now
        );

        return supplierRepository.save(supplier);
    }

    public Supplier findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Fournisseur introuvable avec l'id : " + id
                        )
                );
    }

    public Supplier update(
            Long id,
            String name,
            String contactName,
            String email,
            String phone,
            String address
    ) {

        Supplier supplier = findById(id);

        supplier.setName(name.trim());
        supplier.setContactName(clean(contactName));
        supplier.setEmail(clean(email));
        supplier.setPhone(clean(phone));
        supplier.setAddress(clean(address));
        supplier.setUpdatedAt(Instant.now());

        return supplierRepository.save(supplier);
    }

    public void delete(Long id) {

        Supplier supplier = supplierRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Fournisseur introuvable avec l'id : " + id
                        )
                );

        boolean supplierInUse =
                productRepository.existsBySupplierIdAndStatus(
                        id,
                        ProductStatus.ACTIVE
                );

        if (supplierInUse) {
            throw new SupplierInUseException(
                    "Impossible de supprimer ce fournisseur car il est utilisé par des produits actifs"
            );
        }

        supplierRepository.deleteById(
                supplier.getId()
        );
    }

    public SupplierPageResponse findAll(
            String q,
            int page,
            int size,
            String sort
    ) {

        List<Supplier> suppliers = supplierRepository.findAll();

        if (q != null && !q.isBlank()) {
            String search = q.toLowerCase();

            suppliers = suppliers.stream()
                    .filter(supplier ->
                            supplier.getName()
                                    .toLowerCase()
                                    .contains(search)
                    )
                    .toList();
        }

        boolean descending =
                "name,desc".equalsIgnoreCase(sort);

        Comparator<Supplier> comparator =
                Comparator.comparing(
                        Supplier::getName,
                        String.CASE_INSENSITIVE_ORDER
                );

        if (descending) {
            comparator = comparator.reversed();
        }

        suppliers = suppliers.stream()
                .sorted(comparator)
                .toList();

        long totalElements = suppliers.size();

        int totalPages =
                (int) Math.ceil((double) totalElements / size);

        int fromIndex = page * size;

        if (fromIndex >= totalElements) {
            return new SupplierPageResponse(
                    List.of(),
                    page,
                    size,
                    totalElements,
                    totalPages
            );
        }

        int toIndex =
                Math.min(fromIndex + size, suppliers.size());

        List<SupplierResponse> content =
                suppliers.subList(fromIndex, toIndex)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return new SupplierPageResponse(
                content,
                page,
                size,
                totalElements,
                totalPages
        );
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

    private String clean(String value) {
        return value == null ? null : value.trim();
    }
}