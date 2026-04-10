package com.mktgus.autoatendimento.infrastructure.persistence.mapper;

import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.OrderItem;
import com.mktgus.autoatendimento.infrastructure.persistence.entity.CouponEntity;
import com.mktgus.autoatendimento.infrastructure.persistence.entity.CustomerEntity;
import com.mktgus.autoatendimento.infrastructure.persistence.entity.OrderEntity;
import com.mktgus.autoatendimento.infrastructure.persistence.entity.OrderItemEntity;

import java.util.ArrayList;
import java.util.List;

public final class OrderEntityMapper {
    private OrderEntityMapper() {
    }

    public static OrderEntity toEntity(Order domain) {
        OrderEntity entity = new OrderEntity();
        entity.setId(domain.id());
        entity.setDataHora(domain.orderedAt());
        entity.setValorTotal(domain.totalAmount());

        if (domain.customerCpf() != null) {
            CustomerEntity client = new CustomerEntity();
            client.setCpf(domain.customerCpf());
            entity.setCliente(client);
        }

        if (domain.couponId() != null) {
            CouponEntity coupon = new CouponEntity();
            coupon.setId(domain.couponId());
            entity.setCupom(coupon);
        }

        return entity;
    }

    public static List<OrderItemEntity> toItemEntities(Order domain, OrderEntity savedOrder) {
        List<OrderItemEntity> items = new ArrayList<>();
        for (OrderItem item : domain.items()) {
            OrderItemEntity entity = new OrderItemEntity();
            entity.setPedido(savedOrder);
            entity.setCodigoEan(item.ean());
            entity.setNomeProduto(item.productName());
            entity.setValorProduto(item.unitPrice());
            entity.setQuantidade(item.quantity());
            entity.setProdutoMaiorDeIdade(item.adultOnly());
            entity.setValorItemTotal(item.totalPrice());
            items.add(entity);
        }
        return items;
    }

    public static Order toDomain(OrderEntity entity, List<OrderItemEntity> itemEntities) {
        List<OrderItem> items = itemEntities.stream()
                .map(item -> new OrderItem(
                        item.getCodigoEan(),
                        item.getNomeProduto(),
                        item.getValorProduto(),
                        item.getQuantidade(),
                        item.isProdutoMaiorDeIdade(),
                        item.getValorItemTotal()
                ))
                .toList();

        return new Order(
                entity.getId(),
                entity.getCliente() == null ? null : entity.getCliente().getCpf(),
                entity.getCupom() == null ? null : entity.getCupom().getId(),
                entity.getDataHora(),
                entity.getValorTotal(),
                items
        );
    }
}
