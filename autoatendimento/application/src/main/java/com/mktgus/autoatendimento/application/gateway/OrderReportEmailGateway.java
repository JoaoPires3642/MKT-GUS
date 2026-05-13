package com.mktgus.autoatendimento.application.gateway;

public interface OrderReportEmailGateway {
    void send(String recipientEmail, String subject, String body);
}
