package com.mktgus.autoatendimento.web.ui.controller;

import com.mktgus.autoatendimento.application.mapper.PaymentApiMapper;
import com.mktgus.autoatendimento.application.request.StartPaymentRequest;
import com.mktgus.autoatendimento.application.response.PaymentResponse;
import com.mktgus.autoatendimento.application.usecase.ConfirmPaymentUseCase;
import com.mktgus.autoatendimento.application.usecase.GetPaymentStatusUseCase;
import com.mktgus.autoatendimento.application.usecase.StartPaymentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagamentos")
@CrossOrigin(origins = {"http://localhost:3000", "/swagger-ui", "/api-docs"})
@Tag(name = "Payment", description = "Operações relacionadas a pagamentos digitais")
public class PaymentController {
    private final PaymentApiMapper paymentApiMapper;
    private final StartPaymentUseCase startPaymentUseCase;
    private final GetPaymentStatusUseCase getPaymentStatusUseCase;
    private final ConfirmPaymentUseCase confirmPaymentUseCase;

    public PaymentController(
            PaymentApiMapper paymentApiMapper,
            StartPaymentUseCase startPaymentUseCase,
            GetPaymentStatusUseCase getPaymentStatusUseCase,
            ConfirmPaymentUseCase confirmPaymentUseCase
    ) {
        this.paymentApiMapper = paymentApiMapper;
        this.startPaymentUseCase = startPaymentUseCase;
        this.getPaymentStatusUseCase = getPaymentStatusUseCase;
        this.confirmPaymentUseCase = confirmPaymentUseCase;
    }

    @Operation(summary = "Iniciar uma cobrança digital")
    @ApiResponse(responseCode = "200", description = "Pagamento iniciado",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class)))
    @PostMapping("/iniciar")
    public ResponseEntity<PaymentResponse> iniciar(@RequestBody StartPaymentRequest request) {
        return ResponseEntity.ok(paymentApiMapper.toResponse(startPaymentUseCase.execute(paymentApiMapper.toInput(request))));
    }

    @Operation(summary = "Consultar status do pagamento")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> consultar(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentApiMapper.toResponse(getPaymentStatusUseCase.execute(paymentId)));
    }

    @Operation(summary = "Confirmar pagamento")
    @PostMapping("/{paymentId}/confirmar")
    public ResponseEntity<PaymentResponse> confirmar(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentApiMapper.toResponse(confirmPaymentUseCase.execute(paymentId)));
    }
}
