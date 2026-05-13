package com.mktgus.autoatendimento.infra.data.external.email;

import com.mktgus.autoatendimento.application.gateway.OrderReportEmailGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(JavaMailSender.class)
public class NoOpOrderReportEmailGateway implements OrderReportEmailGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpOrderReportEmailGateway.class);

    @Override
    public void send(String recipientEmail, String subject, String body) {
        LOGGER.warn("Weekly order report email was not sent because SMTP is not configured. Recipient: {}", recipientEmail);
    }
}
