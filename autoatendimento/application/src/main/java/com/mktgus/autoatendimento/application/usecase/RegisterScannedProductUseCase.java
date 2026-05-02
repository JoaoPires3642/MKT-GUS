package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.application.gateway.ScannedProductGateway;
import com.mktgus.autoatendimento.domain.model.Product;
import org.springframework.stereotype.Service;

@Service
public class RegisterScannedProductUseCase {
    private static final String DEFAULT_SESSION_ID = "default";

    private final ScannedProductGateway scannedProductGateway;

    public RegisterScannedProductUseCase(ScannedProductGateway scannedProductGateway) {
        this.scannedProductGateway = scannedProductGateway;
    }

    public void execute(String sessionId, Product product) {
        if (product == null) {
            throw new ValidationException("Produto escaneado invalido.");
        }
        scannedProductGateway.save(normalizeSessionId(sessionId), product);
    }

    private String normalizeSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return DEFAULT_SESSION_ID;
        }
        return sessionId.trim();
    }
}
