package com.stockpilot.product.service;

import com.stockpilot.category.model.Category;
import com.stockpilot.category.repository.CategoryRepository;
import com.stockpilot.common.exception.ProductArchivedException;
import com.stockpilot.common.exception.ProductHasStockException;
import com.stockpilot.common.exception.ProductIdentityLockedException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

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
        Sort.Direction direction =
                sort.toLowerCase().endsWith(",desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        String sortField = sort.split(",")[0];

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (q != null && !q.isBlank()) {
                String search = "%" + q.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), search);
                Predicate skuLike = cb.like(cb.lower(root.get("sku")), search);
                predicates.add(cb.or(nameLike, skuLike));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (supplierId != null) {
                predicates.add(cb.equal(root.get("supplier").get("id"), supplierId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (stockStatus != null) {
                predicates.add(cb.equal(root.get("stockStatus"), stockStatus));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<ProductResponse> content =
                productPage.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return new ProductPageResponse(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }

    public ProductPageResponse findOutOfStock(int page, int size, String sort) {
        Sort.Direction direction =
                sort.toLowerCase().endsWith(",desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        String sortField =
                sort.split(",")[0];

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(direction, sortField)
                );

        Page<Product> productPage = productRepository.findByStockStatus(StockStatus.OUT_OF_STOCK, pageable);

        List<ProductResponse> content =
                productPage.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return new ProductPageResponse(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }

    public Product update(Long id, CreateProductRequest request) {

        Product product = findById(id);

        if (!product.getSku().equals(request.getSku())) {
            throw new ProductIdentityLockedException(
                    "Le SKU d'un produit ne peut pas être modifié"
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