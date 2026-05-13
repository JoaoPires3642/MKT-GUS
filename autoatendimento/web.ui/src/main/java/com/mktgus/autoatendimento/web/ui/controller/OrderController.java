package com.mktgus.autoatendimento.web.ui.controller;

import com.mktgus.autoatendimento.application.mapper.OrderApiMapper;
import com.mktgus.autoatendimento.application.order.OrderAnalyticsReport;
import com.mktgus.autoatendimento.application.order.OrderHistoryFilter;
import com.mktgus.autoatendimento.application.purchase.ConfirmPurchaseInput;
import com.mktgus.autoatendimento.application.request.OrderRequest;
import com.mktgus.autoatendimento.application.response.OrderHistoryResponse;
import com.mktgus.autoatendimento.application.response.OrderResponse;
import com.mktgus.autoatendimento.application.usecase.ConfirmPurchaseUseCase;
import com.mktgus.autoatendimento.application.usecase.GetOrderAnalyticsReportUseCase;
import com.mktgus.autoatendimento.application.usecase.ListCustomerOrderHistoryUseCase;
import com.mktgus.autoatendimento.application.usecase.SearchOrderHistoryUseCase;
import com.mktgus.autoatendimento.application.usecase.SendWeeklyOrderReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = {"http://localhost:3000", "/swagger-ui", "/api-docs"})
@Tag(name = "Order", description = "Operações relacionadas a pedidos")
public class OrderController {

    private final OrderApiMapper orderApiMapper;
    private final ConfirmPurchaseUseCase confirmPurchaseUseCase;
    private final ListCustomerOrderHistoryUseCase listCustomerOrderHistoryUseCase;
    private final SearchOrderHistoryUseCase searchOrderHistoryUseCase;
    private final GetOrderAnalyticsReportUseCase getOrderAnalyticsReportUseCase;
    private final SendWeeklyOrderReportUseCase sendWeeklyOrderReportUseCase;

    public OrderController(OrderApiMapper orderApiMapper,
                           ConfirmPurchaseUseCase confirmPurchaseUseCase,
                           ListCustomerOrderHistoryUseCase listCustomerOrderHistoryUseCase,
                           SearchOrderHistoryUseCase searchOrderHistoryUseCase,
                           GetOrderAnalyticsReportUseCase getOrderAnalyticsReportUseCase,
                           SendWeeklyOrderReportUseCase sendWeeklyOrderReportUseCase) {
        this.orderApiMapper = orderApiMapper;
        this.confirmPurchaseUseCase = confirmPurchaseUseCase;
        this.listCustomerOrderHistoryUseCase = listCustomerOrderHistoryUseCase;
        this.searchOrderHistoryUseCase = searchOrderHistoryUseCase;
        this.getOrderAnalyticsReportUseCase = getOrderAnalyticsReportUseCase;
        this.sendWeeklyOrderReportUseCase = sendWeeklyOrderReportUseCase;
    }

    @Operation(summary = "Confirmar uma compra")
    @ApiResponse(responseCode = "200", description = "Compra confirmada com sucesso",
            content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    @PostMapping("/confirmar-compra")
    public ResponseEntity<OrderResponse> confirmarCompra(@RequestBody OrderRequest request) {
        ConfirmPurchaseInput input = orderApiMapper.toInput(request);
        return ResponseEntity.ok(orderApiMapper.toResponse(confirmPurchaseUseCase.execute(input)));
    }

    @Operation(summary = "Consultar historico de pedidos por CPF")
    @GetMapping("/historico-cliente")
    public ResponseEntity<List<OrderHistoryResponse>> historicoCliente(@RequestParam String cpf) {
        return ResponseEntity.ok(listCustomerOrderHistoryUseCase.execute(cpf));
    }

    @Operation(summary = "Consultar pedidos para operacao")
    @GetMapping("/admin/historico")
    public ResponseEntity<List<OrderHistoryResponse>> historicoOperacao(
            @RequestParam(required = false) Long marketId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(searchOrderHistoryUseCase.execute(new OrderHistoryFilter(marketId, from, to, limit)));
    }

    @Operation(summary = "Consultar relatorio simples de pedidos")
    @GetMapping("/admin/relatorio-simples")
    public ResponseEntity<OrderAnalyticsReport> relatorioSimples(
            @RequestParam(required = false) Long marketId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(getOrderAnalyticsReportUseCase.execute(new OrderHistoryFilter(marketId, from, to, null)));
    }

    @Operation(summary = "Enviar relatorio semanal de pedidos agora")
    @PostMapping("/admin/relatorio-semanal/enviar")
    public ResponseEntity<Void> enviarRelatorioSemanalAgora() {
        sendWeeklyOrderReportUseCase.executeNow();
        return ResponseEntity.accepted().build();
    }
}
