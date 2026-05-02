package com.mktgus.autoatendimento.infra.data.persistence.gateway;

import com.mktgus.autoatendimento.application.gateway.PriceOverrideAuditGateway;
import com.mktgus.autoatendimento.domain.model.PriceOverrideAudit;
import com.mktgus.autoatendimento.infra.data.persistence.mapper.PriceOverrideAuditEntityMapper;
import com.mktgus.autoatendimento.infra.data.persistence.repository.PriceOverrideAuditRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JpaPriceOverrideAuditGateway implements PriceOverrideAuditGateway {
    private final PriceOverrideAuditRepository repository;

    public JpaPriceOverrideAuditGateway(PriceOverrideAuditRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveAll(List<PriceOverrideAudit> audits) {
        repository.saveAll(audits.stream().map(PriceOverrideAuditEntityMapper::toEntity).toList());
    }
}
