package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.application.gateway.OrderGateway;
import com.mktgus.autoatendimento.application.order.OrderAnalyticsReport;
import com.mktgus.autoatendimento.application.order.OrderHistoryFilter;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetOrderAnalyticsReportUseCase {

    private static final int REPORT_ORDER_LIMIT = 5_000;
    private static final int TOP_PRODUCTS_LIMIT = 10;

    private final OrderGateway orderGateway;

    public GetOrderAnalyticsReportUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public OrderAnalyticsReport execute(OrderHistoryFilter filter) {
        if (filter.from() != null && filter.to() != null && filter.from().isAfter(filter.to())) {
            throw new ValidationException("Data inicial nao pode ser maior que a data final.");
        }

        List<Order> orders = orderGateway.search(filter.marketId(), filter.from(), filter.to(), REPORT_ORDER_LIMIT);
        long totalOrders = orders.size();
        double totalRevenue = orders.stream().mapToDouble(Order::totalAmount).sum();
        int itemsSold = orders.stream()
                .flatMap(order -> order.items().stream())
                .mapToInt(OrderItem::quantity)
                .sum();

        return new OrderAnalyticsReport(
                filter.from(),
                filter.to(),
                filter.marketId(),
                totalOrders,
                orders.stream().filter(order -> !order.isAnonymous()).count(),
                orders.stream().filter(Order::isAnonymous).count(),
                itemsSold,
                totalRevenue,
                totalOrders == 0 ? 0 : totalRevenue / totalOrders,
                summarizeTopProducts(orders)
        );
    }

    private List<OrderAnalyticsReport.ProductSalesSummary> summarizeTopProducts(List<Order> orders) {
        Map<String, MutableProductSales> salesByEan = new LinkedHashMap<>();

        orders.stream()
                .flatMap(order -> order.items().stream())
                .forEach(item -> salesByEan
                        .computeIfAbsent(item.ean(), ean -> new MutableProductSales(item.ean(), item.productName()))
                        .add(item.quantity(), item.totalPrice()));

        return salesByEan.values().stream()
                .sorted(Comparator.comparing(MutableProductSales::grossRevenue).reversed())
                .limit(TOP_PRODUCTS_LIMIT)
                .map(product -> new OrderAnalyticsReport.ProductSalesSummary(
                        product.ean,
                        product.productName,
                        product.quantitySold,
                        product.grossRevenue
                ))
                .toList();
    }

    private static final class MutableProductSales {
        private final String ean;
        private final String productName;
        private int quantitySold;
        private double grossRevenue;

        private MutableProductSales(String ean, String productName) {
            this.ean = ean;
            this.productName = productName;
        }

        private void add(int quantity, double revenue) {
            this.quantitySold += quantity;
            this.grossRevenue += revenue;
        }

        private double grossRevenue() {
            return grossRevenue;
        }
    }
}
