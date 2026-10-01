package com.stockpilot.common.exception;
import com.stockpilot.common.dto.ProblemDetailsResponse;
import com.stockpilot.common.exception.NoStockChangeException;
import com.stockpilot.common.exception.StockVersionConflictException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

public class NoStockChangeException extends RuntimeException {

    public NoStockChangeException(String message) {
        super(message);
    }
    @ExceptionHandler(NoStockChangeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetailsResponse handleNoStockChange(
            NoStockChangeException exception,
            HttpServletRequest request
    ) {
        return new ProblemDetailsResponse(
                "urn:stockpilot:problem:no-stock-change",
                "Aucun changement de stock",
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                request.getRequestURI(),
                "NO_STOCK_CHANGE",
                List.of()
        );
    }

}