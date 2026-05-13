package com.mktgus.autoatendimento.app.scheduler;

import com.mktgus.autoatendimento.application.usecase.SendWeeklyOrderReportUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeeklyOrderReportScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyOrderReportScheduler.class);

    private final SendWeeklyOrderReportUseCase sendWeeklyOrderReportUseCase;

    public WeeklyOrderReportScheduler(SendWeeklyOrderReportUseCase sendWeeklyOrderReportUseCase) {
        this.sendWeeklyOrderReportUseCase = sendWeeklyOrderReportUseCase;
    }

    @Scheduled(
            cron = "${reports.weekly-order.cron:0 0 8 * * MON}",
            zone = "${reports.weekly-order.zone:America/Sao_Paulo}"
    )
    public void sendWeeklyOrderReport() {
        LOGGER.info("Starting weekly order report email job.");
        sendWeeklyOrderReportUseCase.executeScheduled();
        LOGGER.info("Weekly order report email job finished.");
    }
}
