package com.mktgus.autoatendimento.infrastructure.persistence.mapper;

import com.mktgus.autoatendimento.domain.model.PriceOverrideAudit;
import com.mktgus.autoatendimento.infrastructure.persistence.entity.PriceOverrideAuditEntity;

public final class PriceOverrideAuditEntityMapper {
    private PriceOverrideAuditEntityMapper() {
    }

    public static PriceOverrideAuditEntity toEntity(PriceOverrideAudit audit) {
        PriceOverrideAuditEntity entity = new PriceOverrideAuditEntity();
        entity.setId(audit.id());
        entity.setOrderId(audit.orderId());
        entity.setEan(audit.ean());
        entity.setProductName(audit.productName());
        entity.setOriginalUnitPrice(audit.originalUnitPrice());
        entity.setAuthorizedUnitPrice(audit.authorizedUnitPrice());
        entity.setQuantity(audit.quantity());
        entity.setEmployeeRegistration(audit.employeeRegistration());
        entity.setReason(audit.reason());
        entity.setAuthorizedAt(audit.authorizedAt());
        return entity;
    }
}
