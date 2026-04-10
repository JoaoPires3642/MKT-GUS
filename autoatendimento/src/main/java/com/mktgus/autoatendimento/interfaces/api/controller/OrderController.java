package com.mktgus.autoatendimento.interfaces.api.controller;

import com.mktgus.autoatendimento.interfaces.api.mapper.OrderApiMapper;
import com.mktgus.autoatendimento.interfaces.api.request.OrderRequest;
import com.mktgus.autoatendimento.interfaces.api.response.OrderResponse;
import com.mktgus.autoatendimento.application.model.ConfirmPurchaseInput;
import com.mktgus.autoatendimento.application.usecase.ConfirmPurchaseUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderApiMapper orderApiMapper;
    private final ConfirmPurchaseUseCase confirmPurchaseUseCase;

    public OrderController(OrderApiMapper orderApiMapper, ConfirmPurchaseUseCase confirmPurchaseUseCase) {
        this.orderApiMapper = orderApiMapper;
        this.confirmPurchaseUseCase = confirmPurchaseUseCase;
    }

    @PostMapping("/confirmar-compra")
    public ResponseEntity<OrderResponse> confirmarCompra(@RequestBody OrderRequest request) {
        ConfirmPurchaseInput input = orderApiMapper.toInput(request);
        return ResponseEntity.ok(orderApiMapper.toResponse(confirmPurchaseUseCase.execute(input)));
    }
}
