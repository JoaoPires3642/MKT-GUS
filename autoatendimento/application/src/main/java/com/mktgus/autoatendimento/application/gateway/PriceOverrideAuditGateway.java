package com.mktgus.autoatendimento.application.gateway;

import com.mktgus.autoatendimento.domain.model.PriceOverrideAudit;

import java.util.List;

public interface PriceOverrideAuditGateway {
    void saveAll(List<PriceOverrideAudit> audits);
}
