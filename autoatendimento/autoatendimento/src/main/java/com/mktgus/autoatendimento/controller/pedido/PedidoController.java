package com.mktgus.autoatendimento.controller.pedido;

import com.mktgus.autoatendimento.Service.pedido.*;
import com.mktgus.autoatendimento.dto.pedidoDTO.*;
import com.mktgus.autoatendimento.Model.pedido.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "http://localhost:3000")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/confirmar-compra")
    public ResponseEntity<Pedido> confirmarCompra(@RequestBody PedidoRequest request) {
        Pedido pedido = pedidoService.confirmarCompra(request);
        return ResponseEntity.ok(pedido);
    }
}