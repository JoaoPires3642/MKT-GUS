package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.application.gateway.OrderGateway;
import com.mktgus.autoatendimento.application.gateway.OrderReportEmailGateway;
import com.mktgus.autoatendimento.application.order.OrderAnalyticsReport;
import com.mktgus.autoatendimento.application.order.OrderHistoryFilter;
import com.mktgus.autoatendimento.application.order.WeeklyOrderReportConfig;
import com.mktgus.autoatendimento.application.response.OrderHistoryResponse;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.OrderItem;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderHistoryUseCasesTest {

    @Test
    void shouldListCustomerHistoryWithMaskedCpf() {
        InMemoryOrderGateway gateway = new InMemoryOrderGateway(List.of(
                order(1L, 1L, 52998224725L, LocalDateTime.of(2026, 5, 10, 12, 0), 20.0),
                order(2L, 1L, 11122233344L, LocalDateTime.of(2026, 5, 10, 13, 0), 30.0)
        ));

        List<OrderHistoryResponse> history = new ListCustomerOrderHistoryUseCase(gateway).execute("529.982.247-25");

        assertEquals(1, history.size());
        assertEquals("***.982.247-**", history.getFirst().customerCpf());
        assertEquals(1, history.getFirst().itemCount());
    }

    @Test
    void shouldRejectInvalidCustomerCpf() {
        ListCustomerOrderHistoryUseCase useCase = new ListCustomerOrderHistoryUseCase(new InMemoryOrderGateway(List.of()));

        assertThrows(ValidationException.class, () -> useCase.execute("123"));
    }

    @Test
    void shouldSearchOperationHistoryByMarketAndPeriod() {
        InMemoryOrderGateway gateway = new InMemoryOrderGateway(List.of(
                order(1L, 1L, 52998224725L, LocalDateTime.of(2026, 5, 10, 12, 0), 20.0),
                order(2L, 2L, 52998224725L, LocalDateTime.of(2026, 5, 11, 12, 0), 30.0),
                order(3L, 1L, null, LocalDateTime.of(2026, 5, 12, 12, 0), 40.0)
        ));

        List<OrderHistoryResponse> history = new SearchOrderHistoryUseCase(gateway).execute(new OrderHistoryFilter(
                1L,
                LocalDateTime.of(2026, 5, 10, 0, 0),
                LocalDateTime.of(2026, 5, 11, 23, 59),
                10
        ));

        assertEquals(1, history.size());
        assertEquals(1L, history.getFirst().id());
    }

    @Test
    void shouldBuildSimpleAnalyticsReport() {
        InMemoryOrderGateway gateway = new InMemoryOrderGateway(List.of(
                new Order(1L, 1L, 52998224725L, null, LocalDateTime.of(2026, 5, 10, 12, 0), 20.0, List.of(
                        new OrderItem("789", "Rice", 10.0, 2, false, 20.0)
                )),
                new Order(2L, 1L, null, null, LocalDateTime.of(2026, 5, 11, 12, 0), 15.0, List.of(
                        new OrderItem("456", "Beans", 15.0, 1, false, 15.0)
                ))
        ));

        OrderAnalyticsReport report = new GetOrderAnalyticsReportUseCase(gateway).execute(new OrderHistoryFilter(1L, null, null, null));

        assertEquals(2, report.totalOrders());
        assertEquals(1, report.identifiedOrders());
        assertEquals(1, report.anonymousOrders());
        assertEquals(3, report.itemsSold());
        assertEquals(35.0, report.totalRevenue());
        assertEquals("789", report.topProducts().getFirst().ean());
    }

    @Test
    void shouldSendWeeklyOrderReportByEmail() {
        InMemoryOrderGateway orderGateway = new InMemoryOrderGateway(List.of(
                new Order(1L, 1L, 52998224725L, null, LocalDateTime.now().minusDays(1), 20.0, List.of(
                        new OrderItem("789", "Rice", 10.0, 2, false, 20.0)
                ))
        ));
        InMemoryOrderReportEmailGateway emailGateway = new InMemoryOrderReportEmailGateway();
        WeeklyOrderReportConfig config = new WeeklyOrderReportConfig();
        config.setRecipientEmail("manager@example.com");
        config.setMarketId(1L);

        SendWeeklyOrderReportUseCase useCase = new SendWeeklyOrderReportUseCase(
                new GetOrderAnalyticsReportUseCase(orderGateway),
                emailGateway,
                config
        );

        useCase.execute();

        assertEquals("manager@example.com", emailGateway.recipientEmail);
        assertEquals(true, emailGateway.subject.startsWith("Weekly order report"));
        assertEquals(true, emailGateway.body.contains("Total orders: 1"));
        assertEquals(true, emailGateway.body.contains("Rice"));
    }

    private static Order order(Long id, Long marketId, Long cpf, LocalDateTime orderedAt, double totalAmount) {
        return new Order(id, marketId, cpf, null, orderedAt, totalAmount, List.of(
                new OrderItem("789", "Rice", totalAmount, 1, false, totalAmount)
        ));
    }

    private static final class InMemoryOrderGateway implements OrderGateway {
        private final List<Order> orders;

        private InMemoryOrderGateway(List<Order> orders) {
            this.orders = orders;
        }

        @Override
        public Order save(Order order) {
            return order;
        }

        @Override
        public Optional<Order> findById(Long id) {
            return orders.stream().filter(order -> id.equals(order.id())).findFirst();
        }

        @Override
        public List<Order> findByCustomerCpf(Long customerCpf) {
            return orders.stream()
                    .filter(order -> customerCpf.equals(order.customerCpf()))
                    .sorted(Comparator.comparing(Order::orderedAt).reversed())
                    .toList();
        }

        @Override
        public List<Order> search(Long marketId, LocalDateTime from, LocalDateTime to, int limit) {
            return orders.stream()
                    .filter(order -> marketId == null || marketId.equals(order.marketId()))
                    .filter(order -> from == null || !order.orderedAt().isBefore(from))
                    .filter(order -> to == null || !order.orderedAt().isAfter(to))
                    .sorted(Comparator.comparing(Order::orderedAt).reversed())
                    .limit(limit)
                    .toList();
        }
    }

    private static final class InMemoryOrderReportEmailGateway implements OrderReportEmailGateway {
        private String recipientEmail;
        private String subject;
        private String body;

        @Override
        public void send(String recipientEmail, String subject, String body) {
            this.recipientEmail = recipientEmail;
            this.subject = subject;
            this.body = body;
        }
    }
}
