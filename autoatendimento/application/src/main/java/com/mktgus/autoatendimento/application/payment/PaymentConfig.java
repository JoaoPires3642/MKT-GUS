package com.mktgus.autoatendimento.application.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {
    private String provider = "fake";
    private long fakeApprovalDelayMs = 2000;
    private long expirationMinutes = 15;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getFakeApprovalDelayMs() {
        return fakeApprovalDelayMs;
    }

    public void setFakeApprovalDelayMs(long fakeApprovalDelayMs) {
        this.fakeApprovalDelayMs = fakeApprovalDelayMs;
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }
}
