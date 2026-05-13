package com.mktgus.autoatendimento.infra.data.external.email;

import com.mktgus.autoatendimento.application.gateway.OrderReportEmailGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(JavaMailSender.class)
public class SmtpOrderReportEmailGateway implements OrderReportEmailGateway {

    private final JavaMailSender javaMailSender;

    public SmtpOrderReportEmailGateway(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void send(String recipientEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }
}
