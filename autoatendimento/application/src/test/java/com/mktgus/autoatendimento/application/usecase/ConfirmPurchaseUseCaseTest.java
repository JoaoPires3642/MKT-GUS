package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.application.purchase.ConfirmPurchaseInput;
import com.mktgus.autoatendimento.application.purchase.ConfirmPurchaseOutput;
import com.mktgus.autoatendimento.application.gateway.ClientGateway;
import com.mktgus.autoatendimento.application.gateway.CouponGateway;
import com.mktgus.autoatendimento.application.gateway.EmployeeGateway;
import com.mktgus.autoatendimento.application.gateway.OrderGateway;
import com.mktgus.autoatendimento.application.gateway.PaymentTransactionGateway;
import com.mktgus.autoatendimento.application.gateway.PriceOverrideAuditGateway;
import com.mktgus.autoatendimento.application.gateway.ProductCatalogGateway;
import com.mktgus.autoatendimento.application.points.PontosConfig;
import com.mktgus.autoatendimento.application.mercado.MercadoConfig;
import com.mktgus.autoatendimento.application.tax.TaxConfig;
import com.mktgus.autoatendimento.domain.model.Coupon;
import com.mktgus.autoatendimento.domain.model.Customer;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.PaymentMethod;
import com.mktgus.autoatendimento.domain.model.PaymentStatus;
import com.mktgus.autoatendimento.domain.model.PaymentTransaction;
import com.mktgus.autoatendimento.domain.model.PriceOverrideAudit;
import com.mktgus.autoatendimento.domain.model.Product;
import com.mktgus.autoatendimento.domain.model.TaxDocument;
import com.mktgus.autoatendimento.domain.model.TaxDocumentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfirmPurchaseUseCaseTest {

    // ----------------------------------------------------------------
    // helpers para não repetir os dois parâmetros novos em cada teste
    // ----------------------------------------------------------------
    private static ConfirmPurchaseUseCase buildUseCase(
            OrderGateway orderGateway,
            ClientGateway clientGateway,
            CouponGateway couponGateway,
            EmployeeGateway employeeGateway,
            PriceOverrideAuditGateway auditGateway,
            FindProductByBarcodeUseCase findProduct,
            PontosConfig pontosConfig,
            MercadoConfig mercadoConfig,
            PaymentTransactionGateway paymentTransactionGateway
    ) {
        return new ConfirmPurchaseUseCase(
                orderGateway,
                clientGateway,
                couponGateway,
                employeeGateway,
                auditGateway,
                findProduct,
                pontosConfig,
                mercadoConfig,
                paymentTransactionGateway,
                new NoOpIssueTaxDocumentUseCase(),
                new TaxConfig()
        );
    }

    // ----------------------------------------------------------------
    // testes
    // ----------------------------------------------------------------

    @Test
    void shouldApplyCouponRulesAndPersistUpdatedPoints() {
        InMemoryClientGateway clientGateway = new InMemoryClientGateway();
        clientGateway.save(new Customer(52998224725L, 100));

        InMemoryCouponGateway couponGateway = new InMemoryCouponGateway();
        couponGateway.coupons.put(1L, new Coupon(1L, "Cupom 10%", "", 10, true, 40, null, 100.0, 15.0));

        InMemoryOrderGateway orderGateway = new InMemoryOrderGateway();
        FindProductByBarcodeUseCase findProductByBarcodeUseCase = new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway());

        PontosConfig pontosConfig = new PontosConfig();
        pontosConfig.setValorPorPonto(6.0);
        pontosConfig.setPontosPorBloco(1);

        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);
        InMemoryPaymentTransactionGateway paymentTransactionGateway = new InMemoryPaymentTransactionGateway();
        Long paymentId = paymentTransactionGateway.save(paidTransaction(108.0)).id();

        ConfirmPurchaseUseCase useCase = buildUseCase(
                orderGateway, clientGateway, couponGateway,
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                findProductByBarcodeUseCase, pontosConfig, mercadoConfig, paymentTransactionGateway
        );

        ConfirmPurchaseOutput output = useCase.execute(new ConfirmPurchaseInput(
                "529.982.247-25",
                List.of(new ConfirmPurchaseInput.Item("789", 2, 60.0, null)),
                new ConfirmPurchaseInput.Coupon(1L, "percentage"),
                paymentId,
                null
        ));

        Order order = output.order();
        assertEquals(108.0, order.totalAmount());
        assertEquals(80, output.updatedPointsBalance());
        assertEquals(80, clientGateway.findByCpf(52998224725L).orElseThrow().points());
    }

    @Test
    void shouldRejectCouponWhenCustomerDoesNotHaveEnoughPoints() {
        InMemoryClientGateway clientGateway = new InMemoryClientGateway();
        clientGateway.save(new Customer(52998224725L, 10));

        InMemoryCouponGateway couponGateway = new InMemoryCouponGateway();
        couponGateway.coupons.put(1L, new Coupon(1L, "Cupom", "", 10, false, 20, null, null, null));

        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);
        InMemoryPaymentTransactionGateway paymentTransactionGateway = new InMemoryPaymentTransactionGateway();
        Long paymentId = paymentTransactionGateway.save(paidTransaction(60.0)).id();

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), clientGateway, couponGateway,
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                new PontosConfig(), mercadoConfig, paymentTransactionGateway
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                "52998224725",
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                new ConfirmPurchaseInput.Coupon(1L, "fixed"),
                paymentId,
                null
        )));
    }

    @Test
    void shouldRejectCouponBelowMinimumPurchase() {
        InMemoryClientGateway clientGateway = new InMemoryClientGateway();
        clientGateway.save(new Customer(52998224725L, 100));

        InMemoryCouponGateway couponGateway = new InMemoryCouponGateway();
        couponGateway.coupons.put(1L, new Coupon(1L, "Cupom", "", 10, true, 20, null, 200.0, null));

        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);
        InMemoryPaymentTransactionGateway paymentTransactionGateway = new InMemoryPaymentTransactionGateway();
        Long paymentId = paymentTransactionGateway.save(paidTransaction(60.0)).id();

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), clientGateway, couponGateway,
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                new PontosConfig(), mercadoConfig, paymentTransactionGateway
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                "52998224725",
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                new ConfirmPurchaseInput.Coupon(1L, "percentage"),
                paymentId,
                null
        )));
    }

    @Test
    void shouldAllowAuthorizedPriceOverrideAndPersistAudit() {
        InMemoryPriceOverrideAuditGateway auditGateway = new InMemoryPriceOverrideAuditGateway();

        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);
        InMemoryPaymentTransactionGateway paymentTransactionGateway = new InMemoryPaymentTransactionGateway();
        Long paymentId = paymentTransactionGateway.save(paidTransaction(45.0)).id();

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), new InMemoryClientGateway(), new InMemoryCouponGateway(),
                new InMemoryEmployeeGateway(), auditGateway,
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                new PontosConfig(), mercadoConfig, paymentTransactionGateway
        );

        ConfirmPurchaseOutput output = useCase.execute(new ConfirmPurchaseInput(
                null,
                List.of(new ConfirmPurchaseInput.Item(
                        "789",
                        1,
                        45.0,
                        new ConfirmPurchaseInput.PriceOverride("12345", 45.0, "ETIQUETA_PROMOCIONAL_NAO_ATUALIZADA")
                )),
                null,
                paymentId,
                null
        ));

        assertEquals(45.0, output.order().totalAmount());
        assertEquals(1, auditGateway.audits.size());
        assertEquals(60.0, auditGateway.audits.getFirst().originalUnitPrice());
        assertEquals(45.0, auditGateway.audits.getFirst().authorizedUnitPrice());
    }

    @Test
    void shouldRejectPriceOverrideWithInvalidReason() {
        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);
        InMemoryPaymentTransactionGateway paymentTransactionGateway = new InMemoryPaymentTransactionGateway();
        Long paymentId = paymentTransactionGateway.save(paidTransaction(45.0)).id();

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), new InMemoryClientGateway(), new InMemoryCouponGateway(),
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                new PontosConfig(), mercadoConfig, paymentTransactionGateway
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                null,
                List.of(new ConfirmPurchaseInput.Item(
                        "789",
                        1,
                        45.0,
                        new ConfirmPurchaseInput.PriceOverride("12345", 45.0, "MOTIVO_LIVRE")
                )),
                null,
                paymentId,
                null
        )));
    }

    @Test
    void shouldRejectCouponFromDifferentMarket() {
        InMemoryClientGateway clientGateway = new InMemoryClientGateway();
        clientGateway.save(new Customer(52998224725L, 100));

        InMemoryCouponGateway couponGateway = new InMemoryCouponGateway();
        couponGateway.coupons.put(1L, new Coupon(1L, "Cupom Outro Mercado", "", 10, true, 0, 99L, null, null));

        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);
        InMemoryPaymentTransactionGateway paymentTransactionGateway = new InMemoryPaymentTransactionGateway();
        Long paymentId = paymentTransactionGateway.save(paidTransaction(60.0)).id();

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), clientGateway, couponGateway,
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                new PontosConfig(), mercadoConfig, paymentTransactionGateway
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                "52998224725",
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                new ConfirmPurchaseInput.Coupon(1L, "percentage"),
                paymentId,
                null
        )));
    }

    @Test
    void shouldRejectPurchaseWhenPaymentIsNotConfirmed() {
        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);
        InMemoryPaymentTransactionGateway paymentTransactionGateway = new InMemoryPaymentTransactionGateway();
        Long paymentId = paymentTransactionGateway.save(processingTransaction(60.0)).id();

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), new InMemoryClientGateway(), new InMemoryCouponGateway(),
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                new PontosConfig(), mercadoConfig, paymentTransactionGateway
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                null,
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                null,
                paymentId,
                null
        )));
    }

    @Test
    void shouldRejectPurchaseWithoutPaymentTransactionId() {
        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), new InMemoryClientGateway(), new InMemoryCouponGateway(),
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                new PontosConfig(), mercadoConfig, new InMemoryPaymentTransactionGateway()
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                null,
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                null,
                null,
                null
        )));
    }

    @Test
    void shouldRejectPurchaseWhenPaymentTransactionDoesNotExist() {
        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), new InMemoryClientGateway(), new InMemoryCouponGateway(),
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                new PontosConfig(), mercadoConfig, new InMemoryPaymentTransactionGateway()
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                null,
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                null,
                999L,
                null
        )));
    }

    @Test
    void shouldRejectPurchaseWhenPaymentTransactionWasAlreadyConsumed() {
        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);
        InMemoryPaymentTransactionGateway paymentTransactionGateway = new InMemoryPaymentTransactionGateway();
        Long paymentId = paymentTransactionGateway.save(consumedPaidTransaction(60.0)).id();

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), new InMemoryClientGateway(), new InMemoryCouponGateway(),
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                new PontosConfig(), mercadoConfig, paymentTransactionGateway
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                null,
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                null,
                paymentId,
                null
        )));
    }

    @Test
    void shouldRejectPurchaseWhenPaidAmountDiffersFromOrderTotal() {
        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);
        InMemoryPaymentTransactionGateway paymentTransactionGateway = new InMemoryPaymentTransactionGateway();
        Long paymentId = paymentTransactionGateway.save(paidTransaction(59.0)).id();

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), new InMemoryClientGateway(), new InMemoryCouponGateway(),
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway()),
                new PontosConfig(), mercadoConfig, paymentTransactionGateway
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                null,
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                null,
                paymentId,
                null
        )));
    }

    @Test
    void shouldRejectAgeRestrictedProductWithoutEmployeeVerification() {
        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);
        InMemoryPaymentTransactionGateway paymentTransactionGateway = new InMemoryPaymentTransactionGateway();
        Long paymentId = paymentTransactionGateway.save(paidTransaction(60.0)).id();

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), new InMemoryClientGateway(), new InMemoryCouponGateway(),
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway(true)),
                new PontosConfig(), mercadoConfig, paymentTransactionGateway
        );

        assertThrows(ValidationException.class, () -> useCase.execute(new ConfirmPurchaseInput(
                null,
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                null,
                paymentId,
                null
        )));
    }

    @Test
    void shouldAllowAgeRestrictedProductWithValidEmployeeVerification() {
        MercadoConfig mercadoConfig = new MercadoConfig();
        mercadoConfig.setId(1L);
        InMemoryPaymentTransactionGateway paymentTransactionGateway = new InMemoryPaymentTransactionGateway();
        Long paymentId = paymentTransactionGateway.save(paidTransaction(60.0)).id();

        ConfirmPurchaseUseCase useCase = buildUseCase(
                new InMemoryOrderGateway(), new InMemoryClientGateway(), new InMemoryCouponGateway(),
                new InMemoryEmployeeGateway(), new InMemoryPriceOverrideAuditGateway(),
                new FindProductByBarcodeUseCase(new InMemoryProductCatalogGateway(true)),
                new PontosConfig(), mercadoConfig, paymentTransactionGateway
        );

        ConfirmPurchaseOutput output = useCase.execute(new ConfirmPurchaseInput(
                null,
                List.of(new ConfirmPurchaseInput.Item("789", 1, 60.0, null)),
                null,
                paymentId,
                "12345"
        ));

        assertEquals(60.0, output.order().totalAmount());
        assertEquals("789", output.order().items().getFirst().ean());
    }

    // ----------------------------------------------------------------
    // stub fiscal — não chama integradora nenhuma
    // ----------------------------------------------------------------
    private static final class NoOpIssueTaxDocumentUseCase extends IssueTaxDocumentUseCase {
        public NoOpIssueTaxDocumentUseCase() {
            super(null, null);
        }

        @Override
        public TaxDocument execute(Order order, TaxDocumentType type) {
            return TaxDocument.pending(order.id(), type);
        }
    }

    // ----------------------------------------------------------------
    // stubs de infraestrutura
    // ----------------------------------------------------------------
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
        private final Map<Long, Order> orders = new HashMap<>();

        @Override
        public Order save(Order order) {
            Order saved = new Order(
                    nextId++, order.marketId(), order.customerCpf(),
                    order.couponId(), LocalDateTime.now(), order.totalAmount(), order.items()
            );
            orders.put(saved.id(), saved);
            return saved;
        }

        @Override
        public Optional<Order> findById(Long id) {
            return Optional.ofNullable(orders.get(id));
        }

        @Override
        public List<Order> findByCustomerCpf(Long customerCpf) {
            return orders.values().stream()
                    .filter(order -> customerCpf.equals(order.customerCpf()))
                    .toList();
        }

        @Override
        public List<Order> search(Long marketId, LocalDateTime from, LocalDateTime to, int limit) {
            return orders.values().stream()
                    .filter(order -> marketId == null || marketId.equals(order.marketId()))
                    .filter(order -> from == null || !order.orderedAt().isBefore(from))
                    .filter(order -> to == null || !order.orderedAt().isAfter(to))
                    .limit(limit)
                    .toList();
        }
    }

    private static final class InMemoryEmployeeGateway implements EmployeeGateway {
        @Override
        public boolean existsByRegistration(Long registration) {
            return 12345L == registration;
        }
    }

    private static final class InMemoryPriceOverrideAuditGateway implements PriceOverrideAuditGateway {
        private final List<PriceOverrideAudit> audits = new ArrayList<>();

        @Override
        public void saveAll(List<PriceOverrideAudit> audits) {
            this.audits.addAll(audits);
        }
    }

    private static final class InMemoryProductCatalogGateway implements ProductCatalogGateway {
        private final boolean adultOnly;

        InMemoryProductCatalogGateway() {
            this(false);
        }

        InMemoryProductCatalogGateway(boolean adultOnly) {
            this.adultOnly = adultOnly;
        }

        @Override
        public Optional<Product> findByBarcode(String barcode) {
            return Optional.of(new Product(barcode, "Produto Teste", null, 60.0, adultOnly, null));
        }
    }

    private static PaymentTransaction paidTransaction(double amount) {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentTransaction(null, "fake", "fake-ref", PaymentMethod.CREDIT, PaymentStatus.PAID, amount, null, now.plusMinutes(15), now, now, now, null);
    }

    private static PaymentTransaction processingTransaction(double amount) {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentTransaction(null, "fake", "fake-ref-processing", PaymentMethod.PIX, PaymentStatus.PROCESSING, amount, null, now.plusMinutes(15), null, now, now, null);
    }

    private static PaymentTransaction consumedPaidTransaction(double amount) {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentTransaction(null, "fake", "fake-ref-consumed", PaymentMethod.PIX, PaymentStatus.PAID, amount, null, now.plusMinutes(15), now, now, now, 99L);
    }

    private static final class InMemoryPaymentTransactionGateway implements PaymentTransactionGateway {
        private final Map<Long, PaymentTransaction> transactions = new HashMap<>();
        private long nextId = 1;

        @Override
        public PaymentTransaction save(PaymentTransaction transaction) {
            PaymentTransaction saved = new PaymentTransaction(
                    transaction.id() == null ? nextId++ : transaction.id(),
                    transaction.provider(),
                    transaction.providerReference(),
                    transaction.method(),
                    transaction.status(),
                    transaction.amount(),
                    transaction.failureReason(),
                    transaction.expiresAt(),
                    transaction.confirmedAt(),
                    transaction.createdAt(),
                    transaction.updatedAt(),
                    transaction.orderId()
            );
            transactions.put(saved.id(), saved);
            return saved;
        }

        @Override
        public Optional<PaymentTransaction> findById(Long id) {
            return Optional.ofNullable(transactions.get(id));
        }
    }
}
