package com.mktgus.autoatendimento.interfaces.api;

import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiMessage> handleValidation(ValidationException exception) {
        return ResponseEntity.badRequest().body(new ApiMessage(exception.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiMessage> handleNotFound(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiMessage(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiMessage> handleGeneric(Exception exception) {
        LOGGER.error("Unhandled API exception", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiMessage("Erro interno do servidor."));
    }

    public record ApiMessage(String message) {
    }
}
