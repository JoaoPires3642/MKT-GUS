package com.mktgus.autoatendimento.infra.utils.controller;

import com.mktgus.autoatendimento.application.verification.VerifyCustomerCpfInput;
import com.mktgus.autoatendimento.application.usecase.VerifyCustomerCpfUseCase;
import io.swagger.v3.oas.annotations.Operation;
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
    @PostMapping("/verificar-cpf")
    public ResponseEntity<Integer> verificarCpf(@RequestBody Map<String, String> requestBody) {
        var customer = verifyCustomerCpfUseCase.execute(new VerifyCustomerCpfInput(requestBody.get("cpf")));
        return ResponseEntity.ok(customer.points());
    }
}
