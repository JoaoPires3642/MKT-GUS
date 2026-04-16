package com.mktgus.autoatendimento.interfaces.api;

import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @ApiResponse(responseCode = "400", description = "Erro de validação",
            content = @Content(schema = @Schema(implementation = ApiMessage.class)))
    public ResponseEntity<ApiMessage> handleValidation(ValidationException exception) {
        return ResponseEntity.badRequest().body(new ApiMessage(exception.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
            content = @Content(schema = @Schema(implementation = ApiMessage.class)))
    public ResponseEntity<ApiMessage> handleNotFound(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiMessage(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
            content = @Content(schema = @Schema(implementation = ApiMessage.class)))
    public ResponseEntity<ApiMessage> handleGeneric(Exception exception) {
        LOGGER.error("Unhandled API exception", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiMessage("Erro interno do servidor."));
    }

    @Schema(name = "ApiMessage", description = "Mensagem de erro da API")
    public record ApiMessage(String message) {
    }
}
