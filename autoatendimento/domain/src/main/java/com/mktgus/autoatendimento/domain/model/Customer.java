package com.mktgus.autoatendimento.domain.model;

public record Customer(Long cpf, int points) {
    public Customer {
        if (cpf == null || !isValidCpf(cpf)) throw new IllegalArgumentException("CPF inválido");
        if (points < 0) throw new IllegalArgumentException("Pontos não podem ser negativos");
    }

    public Customer withPoints(int newPoints) {
        return new Customer(cpf, newPoints);
    }

    public Customer earnPoints(int earned) {
        if (earned < 0) throw new IllegalArgumentException("Pontos ganhos não podem ser negativos");
        return new Customer(cpf, points + earned);
    }

    public boolean hasEnoughPoints(int required) {
        return points >= required;
    }

    private static boolean isValidCpf(Long cpf) {
        if (cpf <= 0) {
            return false;
        }
        String digits = String.format("%011d", cpf);
        if (allDigitsEqual(digits)) {
            return false;
        }
        return calculateCheckDigit(digits, 10) == Character.getNumericValue(digits.charAt(9))
                && calculateCheckDigit(digits, 11) == Character.getNumericValue(digits.charAt(10));
    }

    private static boolean allDigitsEqual(String digits) {
        for (int i = 1; i < digits.length(); i++) {
            if (digits.charAt(i) != digits.charAt(0)) {
                return false;
            }
        }
        return true;
    }

    private static int calculateCheckDigit(String digits, int weightStart) {
        int sum = 0;
        int length = weightStart - 1;
        for (int i = 0; i < length; i++) {
            sum += Character.getNumericValue(digits.charAt(i)) * (weightStart - i);
        }
        int remainder = (sum * 10) % 11;
        return remainder == 10 ? 0 : remainder;
    }
}
