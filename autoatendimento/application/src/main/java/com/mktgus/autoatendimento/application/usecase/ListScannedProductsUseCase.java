package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.gateway.ScannedProductGateway;
import com.mktgus.autoatendimento.domain.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListScannedProductsUseCase {
    private static final String DEFAULT_SESSION_ID = "default";

    private final ScannedProductGateway scannedProductGateway;

    public ListScannedProductsUseCase(ScannedProductGateway scannedProductGateway) {
        this.scannedProductGateway = scannedProductGateway;
    }

    public List<Product> execute(String sessionId) {
        return scannedProductGateway.findBySession(normalizeSessionId(sessionId));
    }

    private String normalizeSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return DEFAULT_SESSION_ID;
        }
        return sessionId.trim();
    }
}
