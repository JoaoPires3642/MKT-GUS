package com.mktgus.autoatendimento.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private static final Long VALID_CPF = 52998224725L;

    @Test
    void shouldCreateCustomerWithValidData() {
        Customer customer = new Customer(VALID_CPF, 100);
        assertEquals(VALID_CPF, customer.cpf());
        assertEquals(100, customer.points());
    }

    @Test
    void shouldRejectNullCpf() {
        assertThrows(IllegalArgumentException.class, () -> new Customer(null, 0));
    }

    @Test
    void shouldRejectNegativeCpf() {
        assertThrows(IllegalArgumentException.class, () -> new Customer(-1L, 0));
    }

    @Test
    void shouldRejectCpfWithInvalidCheckDigits() {
        assertThrows(IllegalArgumentException.class, () -> new Customer(12345678900L, 0));
    }

    @Test
    void shouldRejectNegativePoints() {
        assertThrows(IllegalArgumentException.class, () -> new Customer(VALID_CPF, -1));
    }

    @Test
    void withPointsShouldReturnNewInstanceWithUpdatedPoints() {
        Customer original = new Customer(VALID_CPF, 50);
        Customer updated = original.withPoints(150);
        assertEquals(150, updated.points());
        assertEquals(50, original.points());
    }

    @Test
    void earnPointsShouldAccumulateCorrectly() {
        Customer customer = new Customer(VALID_CPF, 30);
        Customer result = customer.earnPoints(20);
        assertEquals(50, result.points());
    }

    @Test
    void earnPointsShouldRejectNegativeAmount() {
        Customer customer = new Customer(VALID_CPF, 50);
        assertThrows(IllegalArgumentException.class, () -> customer.earnPoints(-10));
    }

    @Test
    void hasEnoughPointsShouldReturnTrueWhenSufficient() {
        Customer customer = new Customer(VALID_CPF, 100);
        assertTrue(customer.hasEnoughPoints(100));
        assertTrue(customer.hasEnoughPoints(50));
    }

    @Test
    void hasEnoughPointsShouldReturnFalseWhenInsufficient() {
        Customer customer = new Customer(VALID_CPF, 30);
        assertFalse(customer.hasEnoughPoints(31));
    }
}
