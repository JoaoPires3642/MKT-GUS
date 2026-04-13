package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.model.ConfirmPurchaseInput;
import com.mktgus.autoatendimento.application.model.ConfirmPurchaseOutput;
import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.domain.gateway.ClientGateway;
import com.mktgus.autoatendimento.domain.gateway.CouponGateway;
import com.mktgus.autoatendimento.domain.gateway.EmployeeGateway;
import com.mktgus.autoatendimento.domain.gateway.OrderGateway;
import com.mktgus.autoatendimento.domain.gateway.PriceOverrideAuditGateway;
import com.mktgus.autoatendimento.domain.model.Coupon;
import com.mktgus.autoatendimento.domain.model.Customer;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.OrderItem;
import com.mktgus.autoatendimento.domain.model.PriceOverrideAudit;
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
    private final EmployeeGateway employeeGateway;
    private final PriceOverrideAuditGateway priceOverrideAuditGateway;
    private final FindProductByBarcodeUseCase findProductByBarcodeUseCase;

    public ConfirmPurchaseUseCase(
            OrderGateway orderGateway,
            ClientGateway clientGateway,
            CouponGateway couponGateway,
            EmployeeGateway employeeGateway,
            PriceOverrideAuditGateway priceOverrideAuditGateway,
            FindProductByBarcodeUseCase findProductByBarcodeUseCase
    ) {
        this.orderGateway = orderGateway;
        this.clientGateway = clientGateway;
        this.couponGateway = couponGateway;
        this.employeeGateway = employeeGateway;
        this.priceOverrideAuditGateway = priceOverrideAuditGateway;
        this.findProductByBarcodeUseCase = findProductByBarcodeUseCase;
    }

    @Transactional
    public ConfirmPurchaseOutput execute(ConfirmPurchaseInput input) {
        Customer client = resolveClient(input.customerCpf());
        PurchaseDraft draft = buildItems(input);
        List<OrderItem> items = draft.items();
        validateItems(items);
        double subtotal = items.stream().mapToDouble(OrderItem::totalPrice).sum();
        Coupon coupon = resolveCoupon(input, subtotal);
        int updatedPointsBalance = updateCustomerPoints(client, subtotal, coupon);

        Order order = new Order(
                null,
                client == null ? null : client.cpf(),
                coupon == null ? null : coupon.id(),
                LocalDateTime.now(),
                applyDiscount(subtotal, coupon),
                items
        );

        Order savedOrder = orderGateway.save(order);
        savePriceOverrideAudits(savedOrder, draft.priceOverrideAudits());
        if (client != null) {
            clientGateway.save(client.withPoints(updatedPointsBalance));
        }
        return new ConfirmPurchaseOutput(savedOrder, client == null ? null : updatedPointsBalance);
    }

    private Customer resolveClient(String rawCpf) {
        if (rawCpf == null || rawCpf.isBlank()) {
            return null;
        }

        try {
            Long cpf = Long.parseLong(rawCpf.replaceAll("\\D", ""));
            return clientGateway.findByCpf(cpf)
                    .orElseThrow(() -> new NotFoundException("Cliente nao encontrado com CPF: " + rawCpf));
        } catch (NumberFormatException exception) {
            throw new ValidationException("CPF invalido: " + rawCpf);
        }
    }

    private void validateItems(List<OrderItem> items) {
        if (items.isEmpty()) {
            throw new ValidationException("O pedido deve possuir ao menos um item.");
        }
    }

    private PurchaseDraft buildItems(ConfirmPurchaseInput input) {
        List<ItemDraft> itemDrafts = input.items().stream().map(itemRequest -> {
            validateQuantity(itemRequest);
            Product product = findProductByBarcodeUseCase.execute(
                    new com.mktgus.autoatendimento.application.model.FindProductByBarcodeInput(itemRequest.ean())
            );
            PriceOverrideAudit pendingAudit = validatePriceAndBuildAudit(product, itemRequest);
            return new ItemDraft(createItem(itemRequest, product), pendingAudit);
        }).toList();

        return new PurchaseDraft(
                itemDrafts.stream().map(ItemDraft::item).toList(),
                itemDrafts.stream()
                        .map(ItemDraft::priceOverrideAudit)
                        .filter(audit -> audit != null)
                        .toList()
        );
    }

    private void validateQuantity(ConfirmPurchaseInput.Item itemRequest) {
        if (itemRequest.quantity() <= 0) {
            throw new ValidationException("Quantidade invalida para o item com EAN: " + itemRequest.ean());
        }
    }

    private PriceOverrideAudit validatePriceAndBuildAudit(Product product, ConfirmPurchaseInput.Item itemRequest) {
        if (Math.abs(product.price() - itemRequest.unitPrice()) <= 0.01) {
            return null;
        }

        ConfirmPurchaseInput.PriceOverride priceOverride = itemRequest.priceOverride();
        if (priceOverride == null) {
            throw new ValidationException("Preco divergente exige autorizacao para o item com EAN: " + itemRequest.ean());
        }

        Long employeeRegistration = parseEmployeeRegistration(priceOverride.employeeRegistration());
        if (!employeeGateway.existsByRegistration(employeeRegistration)) {
            throw new ValidationException("Funcionario nao encontrado para autorizar ajuste de preco.");
        }

        if (priceOverride.reason() == null || priceOverride.reason().isBlank()) {
            throw new ValidationException("Motivo do ajuste de preco e obrigatorio.");
        }

        if (priceOverride.authorizedUnitPrice() <= 0) {
            throw new ValidationException("Valor autorizado para ajuste de preco deve ser positivo.");
        }

        if (Math.abs(priceOverride.authorizedUnitPrice() - itemRequest.unitPrice()) > 0.01) {
            throw new ValidationException("Valor autorizado difere do valor enviado para o item com EAN: " + itemRequest.ean());
        }

        return new PriceOverrideAudit(
                null,
                null,
                itemRequest.ean(),
                product.name(),
                product.price(),
                itemRequest.unitPrice(),
                itemRequest.quantity(),
                employeeRegistration,
                priceOverride.reason().trim(),
                LocalDateTime.now()
        );
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

        if (subtotal <= 0) {
            throw new ValidationException("Nao e possivel aplicar cupom em compra sem valor.");
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

        if (coupon.minimumPurchase() != null && subtotal < coupon.minimumPurchase()) {
            throw new ValidationException("Cupom exige compra minima de: " + coupon.minimumPurchase());
        }

        if (coupon.maximumDiscount() != null) {
            discount = Math.min(discount, coupon.maximumDiscount());
        }

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

        if (coupon.maximumDiscount() != null) {
            discount = Math.min(discount, coupon.maximumDiscount());
        }

        return Math.max(0, subtotal - discount);
    }

    private Long parseEmployeeRegistration(String rawRegistration) {
        if (rawRegistration == null || rawRegistration.isBlank()) {
            throw new ValidationException("Matricula do funcionario e obrigatoria para ajuste de preco.");
        }

        try {
            return Long.parseLong(rawRegistration.replaceAll("\\D", ""));
        } catch (NumberFormatException exception) {
            throw new ValidationException("Matricula do funcionario invalida.");
        }
    }

    private void savePriceOverrideAudits(Order savedOrder, List<PriceOverrideAudit> pendingAudits) {
        if (pendingAudits.isEmpty()) {
            return;
        }

        priceOverrideAuditGateway.saveAll(pendingAudits.stream()
                .map(audit -> new PriceOverrideAudit(
                        audit.id(),
                        savedOrder.id(),
                        audit.ean(),
                        audit.productName(),
                        audit.originalUnitPrice(),
                        audit.authorizedUnitPrice(),
                        audit.quantity(),
                        audit.employeeRegistration(),
                        audit.reason(),
                        audit.authorizedAt()
                ))
                .toList());
    }

    private int updateCustomerPoints(Customer client, double subtotal, Coupon coupon) {
        if (client == null) {
            if (coupon != null) {
                throw new ValidationException("Cupom com pontos exige cliente identificado.");
            }
            return 0;
        }

        int pointsToEarn = calculateEarnedPoints(subtotal);
        int couponCost = coupon == null ? 0 : coupon.cost();

        if (couponCost > client.points()) {
            throw new ValidationException("Saldo de pontos insuficiente para aplicar o cupom.");
        }

        return client.points() - couponCost + pointsToEarn;
    }

    private int calculateEarnedPoints(double subtotal) {
        return ((int) Math.floor(subtotal / 50)) * 10;
    }

    private record PurchaseDraft(List<OrderItem> items, List<PriceOverrideAudit> priceOverrideAudits) {
    }

    private record ItemDraft(OrderItem item, PriceOverrideAudit priceOverrideAudit) {
    }
}
