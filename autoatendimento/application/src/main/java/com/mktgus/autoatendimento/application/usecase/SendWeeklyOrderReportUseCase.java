package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.gateway.OrderReportEmailGateway;
import com.mktgus.autoatendimento.application.order.OrderAnalyticsReport;
import com.mktgus.autoatendimento.application.order.OrderHistoryFilter;
import com.mktgus.autoatendimento.application.order.WeeklyOrderReportConfig;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class SendWeeklyOrderReportUseCase {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final GetOrderAnalyticsReportUseCase getOrderAnalyticsReportUseCase;
    private final OrderReportEmailGateway orderReportEmailGateway;
    private final WeeklyOrderReportConfig weeklyOrderReportConfig;

    public SendWeeklyOrderReportUseCase(GetOrderAnalyticsReportUseCase getOrderAnalyticsReportUseCase,
                                        OrderReportEmailGateway orderReportEmailGateway,
                                        WeeklyOrderReportConfig weeklyOrderReportConfig) {
        this.getOrderAnalyticsReportUseCase = getOrderAnalyticsReportUseCase;
        this.orderReportEmailGateway = orderReportEmailGateway;
        this.weeklyOrderReportConfig = weeklyOrderReportConfig;
    }

    public void execute() {
        executeScheduled();
    }

    public void executeScheduled() {
        if (!weeklyOrderReportConfig.isEnabled()) {
            return;
        }

        executeNow();
    }

    public void executeNow() {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusWeeks(1);
        OrderAnalyticsReport report = getOrderAnalyticsReportUseCase.execute(new OrderHistoryFilter(
                weeklyOrderReportConfig.getMarketId(),
                from,
                to,
                null
        ));

        orderReportEmailGateway.send(
                weeklyOrderReportConfig.getRecipientEmail(),
                buildSubject(from, to),
                buildBody(report)
        );
    }

    private String buildSubject(LocalDateTime from, LocalDateTime to) {
        return "Weekly order report - " + from.toLocalDate() + " to " + to.toLocalDate();
    }

    private String buildBody(OrderAnalyticsReport report) {
        StringBuilder body = new StringBuilder();
        body.append("Weekly order report\n\n");
        body.append("Period: ")
                .append(report.from().format(DATE_TIME_FORMATTER))
                .append(" to ")
                .append(report.to().format(DATE_TIME_FORMATTER))
                .append("\n");
        body.append("Market ID: ").append(report.marketId() == null ? "All" : report.marketId()).append("\n\n");
        body.append("Total orders: ").append(report.totalOrders()).append("\n");
        body.append("Identified orders: ").append(report.identifiedOrders()).append("\n");
        body.append("Anonymous orders: ").append(report.anonymousOrders()).append("\n");
        body.append("Items sold: ").append(report.itemsSold()).append("\n");
        body.append("Total revenue: ").append(String.format(Locale.US, "%.2f", report.totalRevenue())).append("\n");
        body.append("Average ticket: ").append(String.format(Locale.US, "%.2f", report.averageTicket())).append("\n\n");
        body.append("Top products:\n");

        if (report.topProducts().isEmpty()) {
            body.append("- No sales in this period.\n");
            return body.toString();
        }

        report.topProducts().forEach(product -> body
                .append("- ")
                .append(product.productName())
                .append(" (EAN ")
                .append(product.ean())
                .append("): ")
                .append(product.quantitySold())
                .append(" units, revenue ")
                .append(String.format(Locale.US, "%.2f", product.grossRevenue()))
                .append("\n"));

        return body.toString();
    }
}
