package com.mktgus.autoatendimento.application.tax;

/**
 * Resultado retornado pela integradora fiscal.
 * Em caso de sucesso: numero + chave preenchidos.
 * Em caso de falha: motivoFalha preenchido.
 */
public record TaxEmissionResult(
        boolean success,
        String documentNumber,
        String accessKey,
        String danfeUrl,
        String failureReason
) {
    public static TaxEmissionResult success(String number, String key, String danfeUrl) {
        return new TaxEmissionResult(true, number, key, danfeUrl, null);
    }

    public static TaxEmissionResult failure(String reason) {
        return new TaxEmissionResult(false, null, null, null, reason);
    }
}