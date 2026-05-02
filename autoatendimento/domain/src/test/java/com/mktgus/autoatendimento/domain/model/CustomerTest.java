package com.mktgus.autoatendimento.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldCreateCustomerWithValidData() {
        Customer customer = new Customer(12345678900L, 100);
        assertEquals(12345678900L, customer.cpf());
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
    void shouldRejectNegativePoints() {
        assertThrows(IllegalArgumentException.class, () -> new Customer(12345678900L, -1));
    }

    @Test
    void withPointsShouldReturnNewInstanceWithUpdatedPoints() {
        Customer original = new Customer(12345678900L, 50);
        Customer updated = original.withPoints(150);
        assertEquals(150, updated.points());
        assertEquals(50, original.points());
    }

    @Test
    void earnPointsShouldAccumulateCorrectly() {
        Customer customer = new Customer(12345678900L, 30);
        Customer result = customer.earnPoints(20);
        assertEquals(50, result.points());
    }

    @Test
    void earnPointsShouldRejectNegativeAmount() {
        Customer customer = new Customer(12345678900L, 50);
        assertThrows(IllegalArgumentException.class, () -> customer.earnPoints(-10));
    }

    @Test
    void hasEnoughPointsShouldReturnTrueWhenSufficient() {
        Customer customer = new Customer(12345678900L, 100);
        assertTrue(customer.hasEnoughPoints(100));
        assertTrue(customer.hasEnoughPoints(50));
    }

    @Test
    void hasEnoughPointsShouldReturnFalseWhenInsufficient() {
        Customer customer = new Customer(12345678900L, 30);
        assertFalse(customer.hasEnoughPoints(31));
    }
}
