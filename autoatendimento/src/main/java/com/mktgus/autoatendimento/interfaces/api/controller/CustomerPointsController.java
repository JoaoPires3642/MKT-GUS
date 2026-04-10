package com.mktgus.autoatendimento.interfaces.api.controller;

import com.mktgus.autoatendimento.application.model.UpdateCustomerPointsInput;
import com.mktgus.autoatendimento.application.usecase.UpdateCustomerPointsUseCase;
import com.mktgus.autoatendimento.interfaces.api.request.CustomerPointsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pontos")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerPointsController {
    private final UpdateCustomerPointsUseCase updateCustomerPointsUseCase;

    public CustomerPointsController(UpdateCustomerPointsUseCase updateCustomerPointsUseCase) {
        this.updateCustomerPointsUseCase = updateCustomerPointsUseCase;
    }

    @PostMapping("/finalizar-compra")
    public ResponseEntity<ResponseMessage> finalizarCompra(@RequestBody CustomerPointsRequest request) {
        updateCustomerPointsUseCase.execute(new UpdateCustomerPointsInput(request.cpf(), request.requiredPoints()));
        return ResponseEntity.ok(new ResponseMessage("Compra finalizada com sucesso!"));
    }

    public record ResponseMessage(String message) {
    }
}
