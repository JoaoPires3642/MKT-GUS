package com.mktgus.autoatendimento.interfaces.api.controller;

import com.mktgus.autoatendimento.application.model.VerifyCustomerCpfInput;
import com.mktgus.autoatendimento.application.usecase.VerifyCustomerCpfUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pessoa")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {
    private final VerifyCustomerCpfUseCase verifyCustomerCpfUseCase;

    public CustomerController(VerifyCustomerCpfUseCase verifyCustomerCpfUseCase) {
        this.verifyCustomerCpfUseCase = verifyCustomerCpfUseCase;
    }

    @PostMapping("/verificar-cpf")
    public ResponseEntity<Integer> verificarCpf(@RequestBody Map<String, String> requestBody) {
        var customer = verifyCustomerCpfUseCase.execute(new VerifyCustomerCpfInput(requestBody.get("cpf")));
        return ResponseEntity.ok(customer.points());
    }
}
