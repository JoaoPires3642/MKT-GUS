package com.mktgus.autoatendimento.infra.utils.controller;

import com.mktgus.autoatendimento.application.points.UpdateCustomerPointsInput;
import com.mktgus.autoatendimento.application.usecase.UpdateCustomerPointsUseCase;
import com.mktgus.autoatendimento.application.request.CustomerPointsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pontos")
@CrossOrigin(origins = {"http://localhost:3000", "/swagger-ui", "/api-docs"})
@Tag(name = "Customer")
public class CustomerPointsController {
    private final UpdateCustomerPointsUseCase updateCustomerPointsUseCase;

    public CustomerPointsController(UpdateCustomerPointsUseCase updateCustomerPointsUseCase) {
        this.updateCustomerPointsUseCase = updateCustomerPointsUseCase;
    }

    @Operation(summary = "Finalizar compra usando pontos do cliente")
    @PostMapping("/finalizar-compra")
    public ResponseEntity<ResponseMessage> finalizarCompra(@RequestBody CustomerPointsRequest request) {
        updateCustomerPointsUseCase.execute(new UpdateCustomerPointsInput(request.cpf(), request.requiredPoints()));
        return ResponseEntity.ok(new ResponseMessage("Compra finalizada com sucesso!"));
    }

    public record ResponseMessage(String message) {}
}
