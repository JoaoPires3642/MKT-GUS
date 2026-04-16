package com.mktgus.autoatendimento.interfaces.api.controller;

import com.mktgus.autoatendimento.application.model.VerifyCustomerCpfInput;
import com.mktgus.autoatendimento.application.usecase.VerifyCustomerCpfUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pessoa")
@CrossOrigin(origins = {"http://localhost:3000", "/swagger-ui", "/api-docs"})
@Tag(name = "Customer", description = "Operações relacionadas a clientes")
public class CustomerController {
    private final VerifyCustomerCpfUseCase verifyCustomerCpfUseCase;

    public CustomerController(VerifyCustomerCpfUseCase verifyCustomerCpfUseCase) {
        this.verifyCustomerCpfUseCase = verifyCustomerCpfUseCase;
    }

    @Operation(summary = "Verificar CPF e obter pontos do cliente")
    @ApiResponse(responseCode = "200", description = "Pontos do cliente retornados com sucesso")
    @ApiResponse(responseCode = "400", description = "CPF inválido",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiMessage")))
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiMessage")))
    @PostMapping("/verificar-cpf")
    public ResponseEntity<Integer> verificarCpf(@RequestBody Map<String, String> requestBody) {
        var customer = verifyCustomerCpfUseCase.execute(new VerifyCustomerCpfInput(requestBody.get("cpf")));
        return ResponseEntity.ok(customer.points());
    }
}
