package com.stockpilot.common.exception;

import com.stockpilot.common.dto.FieldErrorResponse;
import com.stockpilot.common.dto.ProblemDetailsResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetailsResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {

        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "urn:stockpilot:problem:not-found",
                "Ressource introuvable",
                404,
                exception.getMessage(),
                request.getRequestURI(),
                "RESOURCE_NOT_FOUND",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailsResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        List<FieldErrorResponse> fieldErrors =
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error ->
                                new FieldErrorResponse(
                                        error.getField(),
                                        error.getDefaultMessage()
                                )
                        )
                        .toList();

        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "urn:stockpilot:problem:validation",
                "Requête invalide",
                400,
                "Certains champs sont invalides.",
                request.getRequestURI(),
                "VALIDATION_ERROR",
                fieldErrors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(SkuAlreadyExistsException.class)
    public ResponseEntity<ProblemDetailsResponse> handleSkuAlreadyExists(
            SkuAlreadyExistsException exception,
            HttpServletRequest request
    ) {

        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "urn:stockpilot:problem:sku-already-exists",
                "SKU déjà existant",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "SKU_ALREADY_EXISTS",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(ProductHasStockException.class)
    public ResponseEntity<ProblemDetailsResponse> handleProductHasStock(
            ProductHasStockException exception,
            HttpServletRequest request
    ) {
        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "urn:stockpilot:problem:product-has-stock",
                "Produit avec stock",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "PRODUCT_HAS_STOCK",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(ProductArchivedException.class)
    public ResponseEntity<ProblemDetailsResponse> handleProductArchived(
            ProductArchivedException exception,
            HttpServletRequest request
    ) {
        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "urn:stockpilot:problem:product-archived",
                "Produit déjà archivé",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "PRODUCT_ARCHIVED",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ProblemDetailsResponse> handleInsufficientStock(
            InsufficientStockException exception,
            HttpServletRequest request
    ) {

        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "urn:stockpilot:problem:insufficient-stock",
                "Stock insuffisant",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "INSUFFICIENT_STOCK",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(StockVersionConflictException.class)
    public ResponseEntity<ProblemDetailsResponse> handleStockVersionConflict(
            StockVersionConflictException exception,
            HttpServletRequest request
    ) {
        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "urn:stockpilot:problem:stock-version-conflict",
                "Conflit de version du stock",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "STOCK_VERSION_CONFLICT",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(IdempotencyKeyReusedException.class)
    public ResponseEntity<ProblemDetailsResponse> handleIdempotencyKeyReused(
            IdempotencyKeyReusedException exception,
            HttpServletRequest request
    ) {
        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "urn:stockpilot:problem:idempotency-key-reused",
                "Clé d'idempotence déjà utilisée",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "IDEMPOTENCY_KEY_REUSED",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<ProblemDetailsResponse> handleCategoryInUse(
            CategoryInUseException exception,
            HttpServletRequest request
    ) {
        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "about:blank",
                "Conflit",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "CATEGORY_IN_USE",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(SupplierInUseException.class)
    public ResponseEntity<ProblemDetailsResponse> handleSupplierInUse(
            SupplierInUseException exception,
            HttpServletRequest request
    ) {
        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "about:blank",
                "Conflit",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "SUPPLIER_IN_USE",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(ProductIdentityLockedException.class)
    public ResponseEntity<ProblemDetailsResponse> handleProductIdentityLocked(
            ProductIdentityLockedException exception,
            HttpServletRequest request
    ) {
        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "about:blank",
                "Identité du produit verrouillée",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "PRODUCT_IDENTITY_LOCKED",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(InvalidMovementTypeException.class)
    public ResponseEntity<ProblemDetailsResponse> handleInvalidMovementType(
            InvalidMovementTypeException exception,
            HttpServletRequest request
    ) {
        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "about:blank",
                "Requête invalide",
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "VALIDATION_ERROR",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(NoStockChangeException.class)
    public ResponseEntity<ProblemDetailsResponse> handleNoStockChange(
            NoStockChangeException exception,
            HttpServletRequest request
    ) {
        ProblemDetailsResponse problem = new ProblemDetailsResponse(
                "about:blank",
                "Aucun changement de stock",
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "NO_STOCK_CHANGE",
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}