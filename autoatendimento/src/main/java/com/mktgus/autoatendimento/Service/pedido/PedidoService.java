package com.mktgus.autoatendimento.Service.pedido;

import com.mktgus.autoatendimento.Model.cupom.Cupom;
import com.mktgus.autoatendimento.Model.pedido.ItemPedido;
import com.mktgus.autoatendimento.Model.pedido.Pedido;
import com.mktgus.autoatendimento.Model.pessoa.Cliente;
import com.mktgus.autoatendimento.Service.produto.ProdutoApiService;
import com.mktgus.autoatendimento.dto.pedidoDTO.PedidoRequest;
import com.mktgus.autoatendimento.dto.produtoDTO.ProdutoDto;
import com.mktgus.autoatendimento.repository.cliente.ClienteRepository;
import com.mktgus.autoatendimento.repository.cupom.CupomRepository;
import com.mktgus.autoatendimento.repository.pedido.ItemPedidoRepository;
import com.mktgus.autoatendimento.repository.pedido.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final CupomRepository cupomRepository;
    private final ProdutoApiService produtoApiService;
    private final ItemPedidoRepository itemPedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
                         CupomRepository cupomRepository, ProdutoApiService produtoApiService,
                         ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.cupomRepository = cupomRepository;
        this.produtoApiService = produtoApiService;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Transactional
    public Pedido confirmarCompra(PedidoRequest request) {
        System.out.println("Recebido PedidoRequest: " + request);

        // Validar cliente
        Cliente cliente = null;
        if (request.clienteCpf() != null && !request.clienteCpf().isEmpty()) {
            Long cpf;
            try {
                cpf = Long.parseLong(request.clienteCpf());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("CPF inválido: " + request.clienteCpf());
            }
            cliente = clienteRepository.findByCpf(cpf)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado com CPF: " + request.clienteCpf()));
        }

        // Criar o pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataHora(LocalDateTime.now());

        // Processar itens
        double valorTotal = 0;
        List<ItemPedido> itens = new ArrayList<>();
        for (PedidoRequest.ItemPedidoRequest itemRequest : request.itens()) {
            if (itemRequest.quantidade() <= 0) {
                throw new IllegalArgumentException("Quantidade inválida para o item com EAN: " + itemRequest.ean());
            }

            ProdutoDto produto = produtoApiService.getProdutoPorCodigoDeBarras(itemRequest.ean());
            if (produto == null) {
                throw new RuntimeException("Produto com EAN " + itemRequest.ean() + " não encontrado");
            }

            if (Math.abs(produto.valor() - itemRequest.valorUnitario()) > 0.01) {
                throw new RuntimeException("Valor unitário inconsistente para o item com EAN: " + itemRequest.ean());
            }

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setCodigoEan(itemRequest.ean());
            item.setNomeProduto(produto.nome());
            item.setValorProduto(itemRequest.valorUnitario());
            item.setQuantidade(itemRequest.quantidade());
            item.setProdutoMaiorDeIdade(produto.produtoMaiorDeIdade());
            item.setValorItemTotal(itemRequest.valorUnitario() * itemRequest.quantidade());

            valorTotal += itemRequest.valorUnitario() * itemRequest.quantidade();
            itens.add(item);
        }

        // Validar e aplicar cupom
        Cupom cupom = null;
        double desconto = 0.0;
        if (request.cupom() != null) {
            System.out.println("Buscando cupom com ID: " + request.cupom().id());
            cupom = cupomRepository.findById(request.cupom().id())
                    .orElseThrow(() -> new RuntimeException("Cupom não encontrado com ID: " + request.cupom().id()));
            boolean isPercentual = request.cupom().tipoDesconto().equalsIgnoreCase("percentage");
            boolean descontoEmPorcentual = cupom.isDescontoEmPorcentual(); // true = percentual (%), false = fixo (R$)

// Aqui a lógica correta: se são diferentes, está OK. Se iguais, erro!
            if (isPercentual != descontoEmPorcentual) {
                throw new IllegalArgumentException("Tipo de desconto inconsistente para o cupom ID: " + request.cupom().id() +
                        ". Enviado: " + request.cupom().tipoDesconto() + ", Esperado: " + (descontoEmPorcentual ? "percentage" : "fixed"));
            }

// Cálculo correto:
            desconto = descontoEmPorcentual ? (valorTotal * cupom.getValorDesconto()) / 100 : cupom.getValorDesconto();


            // Validar pontos do cliente, se aplicável (descomente se Cliente tiver campo pontos)
            // if (cliente != null && cupom.getCusto() > cliente.getPontos()) {
            //     throw new IllegalArgumentException("Saldo de pontos insuficiente para usar o cupom ID: " + cupom.getId());
            // }
        }

        // Verificar se o desconto é válido
        if (desconto > valorTotal) {
            throw new IllegalArgumentException("Desconto do cupom (" + desconto + ") não pode ser maior que o valor total (" + valorTotal + ")");
        }

        // Aplicar desconto
        valorTotal = Math.max(0, valorTotal - desconto);
        pedido.setValorTotal(valorTotal);
        pedido.setCupom(cupom);

        // Salvar no banco
        pedidoRepository.save(pedido);
        itemPedidoRepository.saveAll(itens);

        return pedido;
    }
}