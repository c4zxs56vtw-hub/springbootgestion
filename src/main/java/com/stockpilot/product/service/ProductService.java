package com.stockpilot.product.service;

import com.stockpilot.category.model.Category;
import com.stockpilot.category.repository.CategoryRepository;
import com.stockpilot.common.exception.ProductArchivedException;
import com.stockpilot.common.exception.ProductHasStockException;
import com.stockpilot.common.exception.ResourceNotFoundException;
import com.stockpilot.common.exception.SkuAlreadyExistsException;
import com.stockpilot.product.dto.*;
import com.stockpilot.product.model.Product;
import com.stockpilot.product.model.ProductStatus;
import com.stockpilot.product.model.StockStatus;
import com.stockpilot.product.repository.ProductRepository;
import com.stockpilot.supplier.model.Supplier;
import com.stockpilot.supplier.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import com.stockpilot.product.dto.ProductPageResponse;

import java.util.Comparator;
import java.util.List;

import java.time.Instant;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            SupplierRepository supplierRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    public Product create(CreateProductRequest request) {

        String sku = request.getSku().trim();

        if (productRepository.existsBySku(sku)) {
            throw new SkuAlreadyExistsException(
                    "Un produit avec le SKU " + sku + " existe déjà"
            );
        }

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Catégorie introuvable avec l'id : "
                                        + request.getCategoryId()
                        )
                );

        Supplier supplier = null;

        if (request.getSupplierId() != null) {
            supplier = supplierRepository
                    .findById(request.getSupplierId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Fournisseur introuvable avec l'id : "
                                            + request.getSupplierId()
                            )
                    );
        }

        Instant now = Instant.now();

        Product product = new Product(
                null,
                sku,
                request.getName().trim(),
                clean(request.getDescription()),
                category,
                supplier,
                request.getUnit(),
                request.getPurchasePrice().trim(),
                request.getSalePrice().trim(),
                0,
                request.getMinimumStock(),
                ProductStatus.ACTIVE,
                StockStatus.OUT_OF_STOCK,
                0L,
                now,
                now
        );

        return productRepository.save(product);
    }

    public Product findById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Produit introuvable avec l'id : " + id
                        )
                );
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    public ProductResponse toResponse(Product product) {

        CategoryRefResponse category =
                new CategoryRefResponse(
                        product.getCategory().getId(),
                        product.getCategory().getName()
                );

        SupplierRefResponse supplier = null;

        if (product.getSupplier() != null) {
            supplier = new SupplierRefResponse(
                    product.getSupplier().getId(),
                    product.getSupplier().getName()
            );
        }

        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                category,
                supplier,
                product.getUnit(),
                product.getPurchasePrice(),
                product.getSalePrice(),
                product.getQuantityInStock(),
                product.getMinimumStock(),
                product.getStatus(),
                product.getStockStatus(),
                product.getStockVersion(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public ProductPageResponse findAll(
            String q,
            int page,
            int size,
            String sort,
            Long categoryId,
            Long supplierId,
            ProductStatus status,
            StockStatus stockStatus
    ) {

        List<Product> products = productRepository.findAll();

        if (q != null && !q.isBlank()) {
            String search = q.toLowerCase();

            products = products.stream()
                    .filter(product ->
                            product.getName().toLowerCase().contains(search)
                                    ||
                                    product.getSku().toLowerCase().contains(search)
                    )
                    .toList();
        }

        if (categoryId != null) {
            products = products.stream()
                    .filter(product ->
                            product.getCategory() != null
                                    &&
                                    product.getCategory().getId().equals(categoryId)
                    )
                    .toList();
        }

        if (supplierId != null) {
            products = products.stream()
                    .filter(product ->
                            product.getSupplier() != null
                                    &&
                                    product.getSupplier().getId().equals(supplierId)
                    )
                    .toList();
        }

        if (status != null) {
            products = products.stream()
                    .filter(product ->
                            product.getStatus() == status
                    )
                    .toList();
        }

        if (stockStatus != null) {
            products = products.stream()
                    .filter(product ->
                            product.getStockStatus() == stockStatus
                    )
                    .toList();
        }

        Comparator<Product> comparator =
                Comparator.comparing(
                        Product::getName,
                        String.CASE_INSENSITIVE_ORDER
                );

        if ("name,desc".equalsIgnoreCase(sort)) {
            comparator = comparator.reversed();
        }

        products = products.stream()
                .sorted(comparator)
                .toList();

        long totalElements = products.size();

        int totalPages =
                (int) Math.ceil((double) totalElements / size);

        int fromIndex = page * size;

        if (fromIndex >= totalElements) {
            return new ProductPageResponse(
                    List.of(),
                    page,
                    size,
                    totalElements,
                    totalPages
            );
        }

        int toIndex =
                Math.min(fromIndex + size, products.size());

        List<ProductResponse> content =
                products.subList(fromIndex, toIndex)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return new ProductPageResponse(
                content,
                page,
                size,
                totalElements,
                totalPages
        );
    }

    public Product update(Long id, CreateProductRequest request) {

        Product product = findById(id);

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Catégorie introuvable avec l'id : "
                                        + request.getCategoryId()
                        )
                );

        Supplier supplier = null;

        if (request.getSupplierId() != null) {
            supplier = supplierRepository
                    .findById(request.getSupplierId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Fournisseur introuvable avec l'id : "
                                            + request.getSupplierId()
                            )
                    );
        }

        product.setName(request.getName().trim());
        product.setDescription(clean(request.getDescription()));
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setUnit(request.getUnit());
        product.setPurchasePrice(request.getPurchasePrice().trim());
        product.setSalePrice(request.getSalePrice().trim());
        product.setMinimumStock(request.getMinimumStock());
        product.setUpdatedAt(Instant.now());

        return productRepository.save(product);
    }
    public Product archive(Long id) {

        Product product = findById(id);

        if (product.getStatus() == ProductStatus.ARCHIVED) {
            throw new ProductArchivedException(
                    "Le produit est déjà archivé"
            );
        }

        if (product.getQuantityInStock() != 0) {
            throw new ProductHasStockException(
                    "Impossible d'archiver le produit car son stock n'est pas à 0"
            );
        }

        product.setStatus(ProductStatus.ARCHIVED);
        product.setUpdatedAt(Instant.now());

        return productRepository.save(product);
    }
}