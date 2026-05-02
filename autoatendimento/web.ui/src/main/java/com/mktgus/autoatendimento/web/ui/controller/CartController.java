package com.mktgus.autoatendimento.web.ui.controller;

import com.mktgus.autoatendimento.application.cart.CartSnapshot;
import com.mktgus.autoatendimento.application.cart.SaveCartInput;
import com.mktgus.autoatendimento.application.request.SaveCartRequest;
import com.mktgus.autoatendimento.application.response.CartResponse;
import com.mktgus.autoatendimento.application.usecase.RecoverCartUseCase;
import com.mktgus.autoatendimento.application.usecase.SaveCartUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/carrinho")
@CrossOrigin(origins = {"http://localhost:3000", "/swagger-ui", "/api-docs"})
@Tag(name = "Cart", description = "Recuperação de carrinho cancelado (TTL: 5 minutos, exclusivo por CPF)")
public class CartController {

    private final SaveCartUseCase saveCartUseCase;
    private final RecoverCartUseCase recoverCartUseCase;

    public CartController(SaveCartUseCase saveCartUseCase, RecoverCartUseCase recoverCartUseCase) {
        this.saveCartUseCase = saveCartUseCase;
        this.recoverCartUseCase = recoverCartUseCase;
    }

    @Operation(summary = "Salvar carrinho ao cancelar compra - disponível por 5 minutos para o mesmo CPF")
    @PostMapping("/salvar")
    public ResponseEntity<Void> salvarCarrinho(@RequestBody SaveCartRequest request) {
        var input = new SaveCartInput(
                request.cpf(),
                request.items().stream()
                        .map(i -> new SaveCartInput.CartItemInput(
                                i.ean(), i.productName(), i.unitPrice(), i.quantity(), i.adultOnly()))
                        .toList()
        );
        saveCartUseCase.execute(input);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Recuperar carrinho cancelado - apenas o próprio CPF pode ver seu carrinho")
    @GetMapping("/recuperar")
    public ResponseEntity<CartResponse> recuperarCarrinho(@RequestParam String cpf) {
        Optional<CartSnapshot> snapshot = recoverCartUseCase.execute(cpf);
        return snapshot.map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    private CartResponse toResponse(CartSnapshot snapshot) {
        var items = snapshot.items().stream()
                .map(i -> new CartResponse.CartItemResponse(
                        i.ean(), i.productName(), i.unitPrice(), i.quantity(), i.adultOnly()))
                .toList();
        return new CartResponse(snapshot.cpf(), snapshot.savedAt(), items);
    }
}
