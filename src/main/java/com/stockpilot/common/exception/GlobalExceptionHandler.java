package com.stockpilot.common.exception;

import com.stockpilot.common.dto.FieldErrorResponse;
import com.stockpilot.common.dto.ProblemDetailsResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetailsResponse handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {

        return new ProblemDetailsResponse(
                "urn:stockpilot:problem:not-found",
                "Ressource introuvable",
                404,
                exception.getMessage(),
                request.getRequestURI(),
                "RESOURCE_NOT_FOUND",
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetailsResponse handleValidation(
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

        return new ProblemDetailsResponse(
                "urn:stockpilot:problem:validation",
                "Requête invalide",
                400,
                "Certains champs sont invalides.",
                request.getRequestURI(),
                "VALIDATION_ERROR",
                fieldErrors
        );
    }
    @ExceptionHandler(SkuAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetailsResponse handleSkuAlreadyExists(
            SkuAlreadyExistsException exception,
            HttpServletRequest request
    ) {

        return new ProblemDetailsResponse(
                "urn:stockpilot:problem:sku-already-exists",
                "SKU déjà existant",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "SKU_ALREADY_EXISTS",
                List.of()
        );
    }
    @ExceptionHandler(ProductHasStockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetailsResponse handleProductHasStock(
            ProductHasStockException exception,
            HttpServletRequest request
    ) {
        return new ProblemDetailsResponse(
                "urn:stockpilot:problem:product-has-stock",
                "Produit avec stock",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "PRODUCT_HAS_STOCK",
                List.of()
        );
    }
    @ExceptionHandler(ProductArchivedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetailsResponse handleProductArchived(
            ProductArchivedException exception,
            HttpServletRequest request
    ) {
        return new ProblemDetailsResponse(
                "urn:stockpilot:problem:product-archived",
                "Produit déjà archivé",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "PRODUCT_ARCHIVED",
                List.of()
        );
    }
}