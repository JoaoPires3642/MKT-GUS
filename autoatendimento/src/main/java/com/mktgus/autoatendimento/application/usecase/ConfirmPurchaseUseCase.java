package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.model.ConfirmPurchaseInput;
import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.domain.gateway.ClientGateway;
import com.mktgus.autoatendimento.domain.gateway.CouponGateway;
import com.mktgus.autoatendimento.domain.gateway.OrderGateway;
import com.mktgus.autoatendimento.domain.model.Coupon;
import com.mktgus.autoatendimento.domain.model.Customer;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.OrderItem;
import com.mktgus.autoatendimento.domain.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConfirmPurchaseUseCase {
    private final OrderGateway orderGateway;
    private final ClientGateway clientGateway;
    private final CouponGateway couponGateway;
    private final FindProductByBarcodeUseCase findProductByBarcodeUseCase;

    public ConfirmPurchaseUseCase(
            OrderGateway orderGateway,
            ClientGateway clientGateway,
            CouponGateway couponGateway,
            FindProductByBarcodeUseCase findProductByBarcodeUseCase
    ) {
        this.orderGateway = orderGateway;
        this.clientGateway = clientGateway;
        this.couponGateway = couponGateway;
        this.findProductByBarcodeUseCase = findProductByBarcodeUseCase;
    }

    @Transactional
    public Order execute(ConfirmPurchaseInput input) {
        Customer client = resolveClient(input.customerCpf());
        List<OrderItem> items = buildItems(input);
        double subtotal = items.stream().mapToDouble(OrderItem::totalPrice).sum();
        Coupon coupon = resolveCoupon(input, subtotal);

        Order order = new Order(
                null,
                client == null ? null : client.cpf(),
                coupon == null ? null : coupon.id(),
                LocalDateTime.now(),
                applyDiscount(subtotal, coupon),
                items
        );

        return orderGateway.save(order);
    }

    private Customer resolveClient(String rawCpf) {
        if (rawCpf == null || rawCpf.isBlank()) {
            return null;
        }

        try {
            Long cpf = Long.parseLong(rawCpf);
            return clientGateway.findByCpf(cpf)
                    .orElseThrow(() -> new NotFoundException("Cliente nao encontrado com CPF: " + rawCpf));
        } catch (NumberFormatException exception) {
            throw new ValidationException("CPF invalido: " + rawCpf);
        }
    }

    private List<OrderItem> buildItems(ConfirmPurchaseInput input) {
        return input.items().stream().map(itemRequest -> {
            validateQuantity(itemRequest);
            Product product = findProductByBarcodeUseCase.execute(
                    new com.mktgus.autoatendimento.application.model.FindProductByBarcodeInput(itemRequest.ean())
            );
            validateUnitPrice(product, itemRequest);
            return createItem(itemRequest, product);
        }).toList();
    }

    private void validateQuantity(ConfirmPurchaseInput.Item itemRequest) {
        if (itemRequest.quantity() <= 0) {
            throw new ValidationException("Quantidade invalida para o item com EAN: " + itemRequest.ean());
        }
    }

    private void validateUnitPrice(Product product, ConfirmPurchaseInput.Item itemRequest) {
        if (Math.abs(product.price() - itemRequest.unitPrice()) > 0.01) {
            throw new ValidationException("Valor unitario inconsistente para o item com EAN: " + itemRequest.ean());
        }
    }

    private OrderItem createItem(ConfirmPurchaseInput.Item itemRequest, Product product) {
        return new OrderItem(
                itemRequest.ean(),
                product.name(),
                itemRequest.unitPrice(),
                itemRequest.quantity(),
                product.adultOnly(),
                itemRequest.unitPrice() * itemRequest.quantity()
        );
    }

    private Coupon resolveCoupon(ConfirmPurchaseInput input, double subtotal) {
        if (input.coupon() == null) {
            return null;
        }

        Coupon coupon = couponGateway.findById(input.coupon().id())
                .orElseThrow(() -> new NotFoundException("Cupom nao encontrado com ID: " + input.coupon().id()));

        boolean requestIsPercentage = "percentage".equalsIgnoreCase(input.coupon().discountType());
        if (requestIsPercentage != coupon.percentageDiscount()) {
            throw new ValidationException("Tipo de desconto inconsistente para o cupom ID: " + input.coupon().id());
        }

        double discount = coupon.percentageDiscount()
                ? (subtotal * coupon.discountValue()) / 100
                : coupon.discountValue();

        if (discount > subtotal) {
            throw new ValidationException("Desconto do cupom nao pode ser maior que o valor total da compra.");
        }

        return coupon;
    }

    private double applyDiscount(double subtotal, Coupon coupon) {
        if (coupon == null) {
            return subtotal;
        }

        double discount = coupon.percentageDiscount()
                ? (subtotal * coupon.discountValue()) / 100
                : coupon.discountValue();

        return Math.max(0, subtotal - discount);
    }
}
