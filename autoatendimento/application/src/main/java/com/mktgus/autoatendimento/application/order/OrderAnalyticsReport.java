package com.mktgus.autoatendimento.application.order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderAnalyticsReport(
        LocalDateTime from,
        LocalDateTime to,
        Long marketId,
        long totalOrders,
        long identifiedOrders,
        long anonymousOrders,
        int itemsSold,
        double totalRevenue,
        double averageTicket,
        List<ProductSalesSummary> topProducts
) {
    public record ProductSalesSummary(
            String ean,
            String productName,
            int quantitySold,
            double grossRevenue
    ) {
    }
}
