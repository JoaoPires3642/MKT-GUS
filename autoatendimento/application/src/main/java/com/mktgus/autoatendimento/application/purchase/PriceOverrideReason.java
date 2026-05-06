package com.mktgus.autoatendimento.application.purchase;

import java.util.Arrays;

public enum PriceOverrideReason {
    ETIQUETA_PROMOCIONAL_NAO_ATUALIZADA,
    PROMOCAO_NAO_SINCRONIZADA,
    ETIQUETA_DESATUALIZADA,
    AJUSTE_OPERACIONAL;

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return Arrays.stream(values()).anyMatch(reason -> reason.name().equals(value.trim()));
    }
}
