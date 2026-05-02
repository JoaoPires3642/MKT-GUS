package com.mktgus.autoatendimento.domain.model;

import java.time.LocalDateTime;

public record PriceOverrideAudit(
        Long id,
        Long orderId,
        String ean,
        String productName,
        double originalUnitPrice,
        double authorizedUnitPrice,
        int quantity,
        Long employeeRegistration,
        String reason,
        LocalDateTime authorizedAt
) {
}
