package com.mktgus.autoatendimento.dto.pedidoDTO;

import java.util.List;

public record PedidoRequest(
        String clienteCpf, // Pode ser null
        List<ItemPedidoRequest> itens,
        CupomRequest cupom // Pode ser null
) {
    public record ItemPedidoRequest(
            String ean,
            int quantidade,
            double valorUnitario
    ) {}

    public record CupomRequest(
            Long id,
            double desconto,
            String tipoDesconto
    ) {}
}