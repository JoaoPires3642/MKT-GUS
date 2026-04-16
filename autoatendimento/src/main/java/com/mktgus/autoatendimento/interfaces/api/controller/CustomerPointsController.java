package com.mktgus.autoatendimento.interfaces.api.controller;

import com.mktgus.autoatendimento.application.model.UpdateCustomerPointsInput;
import com.mktgus.autoatendimento.application.usecase.UpdateCustomerPointsUseCase;
import com.mktgus.autoatendimento.interfaces.api.request.CustomerPointsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pontos")
@CrossOrigin(origins = {"http://localhost:3000", "/swagger-ui", "/api-docs"})
@Tag(name = "Customer", description = "Operações relacionadas a pontos de clientes")
public class CustomerPointsController {
    private final UpdateCustomerPointsUseCase updateCustomerPointsUseCase;

    public CustomerPointsController(UpdateCustomerPointsUseCase updateCustomerPointsUseCase) {
        this.updateCustomerPointsUseCase = updateCustomerPointsUseCase;
    }

    @Operation(summary = "Finalizar compra usando pontos do cliente")
    @ApiResponse(responseCode = "200", description = "Compra finalizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiMessage")))
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiMessage")))
    @PostMapping("/finalizar-compra")
    public ResponseEntity<ResponseMessage> finalizarCompra(@RequestBody CustomerPointsRequest request) {
        updateCustomerPointsUseCase.execute(new UpdateCustomerPointsInput(request.cpf(), request.requiredPoints()));
        return ResponseEntity.ok(new ResponseMessage("Compra finalizada com sucesso!"));
    }

    public record ResponseMessage(String message) {
    }
}
