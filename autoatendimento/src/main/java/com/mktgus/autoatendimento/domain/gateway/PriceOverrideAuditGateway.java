package com.mktgus.autoatendimento.domain.gateway;

import com.mktgus.autoatendimento.domain.model.PriceOverrideAudit;

import java.util.List;

public interface PriceOverrideAuditGateway {
    void saveAll(List<PriceOverrideAudit> audits);
}
