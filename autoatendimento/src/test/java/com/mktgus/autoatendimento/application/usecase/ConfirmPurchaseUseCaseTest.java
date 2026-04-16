package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.application.model.ConfirmPurchaseInput;
import com.mktgus.autoatendimento.application.model.ConfirmPurchaseOutput;
import com.mktgus.autoatendimento.domain.gateway.ClientGateway;
import com.mktgus.autoatendimento.domain.gateway.CouponGateway;
import com.mktgus.autoatendimento.domain.gateway.EmployeeGateway;
import com.mktgus.autoatendimento.domain.gateway.OrderGateway;
import com.mktgus.autoatendimento.domain.gateway.PriceOverrideAuditGateway;
import com.mktgus.autoatendimento.domain.gateway.ProductCatalogGateway;
import com.mktgus.autoatendimento.infrastructure.config.PontosConfig;
import com.mktgus.autoatendimento.domain.model.Coupon;
import com.mktgus.autoatendimento.domain.model.Customer;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.PriceOverrideAudit;
import com.mktgus.autoatendimento.domain.model.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfirmPurchaseUseCaseTest {
    @Test
    void shouldApplyCouponRulesAndPersistUpdatedPoints() {
        InMemoryClientGateway clientGateway = new InMemoryClientGateway();
        clientGateway.save(new Customer(12345678900L, 100));

        InMemoryCouponGateway couponGateway = new InMemoryCouponGateway();
        couponGateway.coupons.put(1L, new Coupon(1L, "Cupom 10%", "", 10, true, 40, 100.0, 15.0));

        InMemoryOrderGateway orderGateway = new InMemoryOrderGateway();
        FindProductByBarcodeUseCase findProductByBarcodeUseCase = new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway());

        PontosConfig pontosConfig = new PontosConfig();

        ConfirmPurchaseUseCase useCase = new ConfirmPurchaseUseCase(
                orderGateway,
                clientGateway,
                couponGateway,
                new InMemoryEmployeeGateway(),
                new InMemoryPriceOverrideAuditGateway(),
                findProductByBarcodeUseCase,
                pontosConfig
        );

        ConfirmPurchaseOutput output = useCase.execute(new ConfirmPurchaseInput(
                "123.456.789-00",
                List.of(new ConfirmPurchaseInput.Item("789", 2, 60.0, null)),
                new ConfirmPurchaseInput.Coupon(1L, "percentage")
        ));

        Order order = output.order();
        assertEquals(108.0, order.totalAmount());
        assertEquals(80, output.updatedPointsBalance());
        assertEquals(80, clientGateway.findByCpf(12345678900L).orElseThrow().points());
    }

    @Test
    void shouldRejectCouponWhenCustomerDoesNotHaveEnoughPoints() {
        InMemoryClientGateway clientGateway = new InMemoryClientGateway();
        clientGateway.save(new Customer(12345678900L, 10));

        InMemoryCouponGateway couponGateway = new InMemoryCouponGateway();
        couponGateway.coupons.put(1L, new Coupon(1L, "Cupom", "", 10, false, 20, null, null));

        PontosConfig pontosConfig = new PontosConfig();

        ConfirmPurchaseUseCase useCase = new ConfirmPurchaseUseCase(
                new InMemoryOrderGateway(),
                clientGateway,
                couponGateway,
                new InMemoryEmployeeGateway(),
                new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                pontosConfig
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                "12345678900",
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                new ConfirmPurchaseInput.Coupon(1L, "fixed")
        )));
    }

    @Test
    void shouldRejectCouponBelowMinimumPurchase() {
        InMemoryClientGateway clientGateway = new InMemoryClientGateway();
        clientGateway.save(new Customer(12345678900L, 100));

        InMemoryCouponGateway couponGateway = new InMemoryCouponGateway();
        couponGateway.coupons.put(1L, new Coupon(1L, "Cupom", "", 10, true, 20, 200.0, null));

        PontosConfig pontosConfig = new PontosConfig();

        ConfirmPurchaseUseCase useCase = new ConfirmPurchaseUseCase(
                new InMemoryOrderGateway(),
                clientGateway,
                couponGateway,
                new InMemoryEmployeeGateway(),
                new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                pontosConfig
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                "12345678900",
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                new ConfirmPurchaseInput.Coupon(1L, "percentage")
        )));
    }

    @Test
    void shouldAllowAuthorizedPriceOverrideAndPersistAudit() {
        InMemoryPriceOverrideAuditGateway auditGateway = new InMemoryPriceOverrideAuditGateway();

        PontosConfig pontosConfig = new PontosConfig();

        ConfirmPurchaseUseCase useCase = new ConfirmPurchaseUseCase(
                new InMemoryOrderGateway(),
                new InMemoryClientGateway(),
                new InMemoryCouponGateway(),
                new InMemoryEmployeeGateway(),
                auditGateway,
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                pontosConfig
        );

        ConfirmPurchaseOutput output = useCase.execute(new ConfirmPurchaseInput(
                null,
                List.of(new ConfirmPurchaseInput.Item(
                        "789",
                        1,
                        45.0,
                        new ConfirmPurchaseInput.PriceOverride("12345", 45.0, "ETIQUETA_PROMOCIONAL_NAO_ATUALIZADA")
                )),
                null
        ));

        assertEquals(45.0, output.order().totalAmount());
        assertEquals(1, auditGateway.audits.size());
        assertEquals(60.0, auditGateway.audits.getFirst().originalUnitPrice());
        assertEquals(45.0, auditGateway.audits.getFirst().authorizedUnitPrice());
    }

    private static final class InMemoryClientGateway implements ClientGateway {
        private final Map<Long, Customer> customers = new HashMap<>();

        @Override
        public Optional<Customer> findByCpf(Long cpf) {
            return Optional.ofNullable(customers.get(cpf));
        }

        @Override
        public Customer save(Customer client) {
            customers.put(client.cpf(), client);
            return client;
        }
    }

    private static final class InMemoryCouponGateway implements CouponGateway {
        private final Map<Long, Coupon> coupons = new HashMap<>();

        @Override
        public List<Coupon> findAll() {
            return coupons.values().stream().toList();
        }

        @Override
        public Optional<Coupon> findById(Long id) {
            return Optional.ofNullable(coupons.get(id));
        }
    }

    private static final class InMemoryOrderGateway implements OrderGateway {
        private long nextId = 1;

        @Override
        public Order save(Order order) {
            return new Order(nextId++, order.customerCpf(), order.couponId(), LocalDateTime.now(), order.totalAmount(), order.items());
        }
    }

    private static final class InMemoryEmployeeGateway implements EmployeeGateway {
        @Override
        public boolean existsByRegistration(Long registration) {
            return 12345L == registration;
        }
    }

    private static final class InMemoryPriceOverrideAuditGateway implements PriceOverrideAuditGateway {
        private final List<PriceOverrideAudit> audits = new java.util.ArrayList<>();

        @Override
        public void saveAll(List<PriceOverrideAudit> audits) {
            this.audits.addAll(audits);
        }
    }

    private static final class InMemoryProductCatalogGateway implements ProductCatalogGateway {
        @Override
        public Optional<Product> findByBarcode(String barcode) {
            return Optional.of(new Product(barcode, "Produto Teste", null, 60.0, false));
        }
    }
}
