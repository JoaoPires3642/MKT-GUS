package com.mktgus.autoatendimento.application.tax;

import com.mktgus.autoatendimento.domain.model.TaxDocumentType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuração fiscal lida do application.yml:
 *
 * fiscal:
 *   type: NFCE                # NFCE | SAT
 *   emission-enabled: false   # false enquanto módulo não implementado
 */
@Component
@ConfigurationProperties(prefix = "fiscal")
public class TaxConfig {

    private TaxDocumentType type = TaxDocumentType.NFCE;
    private boolean emissionEnabled = false;

    public TaxDocumentType getType() {
        return type;
    }

    public void setType(TaxDocumentType type) {
        this.type = type;
    }

    public boolean isEmissionEnabled() {
        return emissionEnabled;
    }

    public void setEmissionEnabled(boolean emissionEnabled) {
        this.emissionEnabled = emissionEnabled;
    }
}