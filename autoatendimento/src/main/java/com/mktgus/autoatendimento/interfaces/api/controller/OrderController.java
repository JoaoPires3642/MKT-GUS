package com.mktgus.autoatendimento.interfaces.api.controller;

import com.mktgus.autoatendimento.interfaces.api.mapper.OrderApiMapper;
import com.mktgus.autoatendimento.interfaces.api.request.OrderRequest;
import com.mktgus.autoatendimento.interfaces.api.response.OrderResponse;
import com.mktgus.autoatendimento.application.model.ConfirmPurchaseInput;
import com.mktgus.autoatendimento.application.usecase.ConfirmPurchaseUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = {"http://localhost:3000", "/swagger-ui", "/api-docs"})
@Tag(name = "Order", description = "Operações relacionadas a pedidos")
public class OrderController {

    private final OrderApiMapper orderApiMapper;
    private final ConfirmPurchaseUseCase confirmPurchaseUseCase;

    public OrderController(OrderApiMapper orderApiMapper, ConfirmPurchaseUseCase confirmPurchaseUseCase) {
        this.orderApiMapper = orderApiMapper;
        this.confirmPurchaseUseCase = confirmPurchaseUseCase;
    }

    @Operation(summary = "Confirmar uma compra")
    @ApiResponse(responseCode = "200", description = "Compra confirmada com sucesso",
            content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos para compra",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiMessage")))
    @ApiResponse(responseCode = "404", description = "Cliente ou cupom não encontrado",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiMessage")))
    @PostMapping("/confirmar-compra")
    public ResponseEntity<OrderResponse> confirmarCompra(@RequestBody OrderRequest request) {
        ConfirmPurchaseInput input = orderApiMapper.toInput(request);
        return ResponseEntity.ok(orderApiMapper.toResponse(confirmPurchaseUseCase.execute(input)));
    }
}
