package com.mktgus.autoatendimento.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void factoryShouldComputeTotalPrice() {
        OrderItem item = OrderItem.of("7891234567890", "Biscoito", 5.0, 3, false);
        assertEquals(15.0, item.totalPrice(), 0.001);
        assertEquals(3, item.quantity());
    }

    @Test
    void factoryShouldRejectNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> OrderItem.of("123", "Nome", -1.0, 1, false));
    }

    @Test
    void factoryShouldRejectZeroQuantity() {
        assertThrows(IllegalArgumentException.class, () -> OrderItem.of("123", "Nome", 5.0, 0, false));
    }

    @Test
    void requiresAgeVerificationShouldReflectAdultOnlyFlag() {
        OrderItem adult = OrderItem.of("123", "Vodka", 20.0, 1, true);
        OrderItem regular = OrderItem.of("456", "Água", 2.0, 2, false);
        assertTrue(adult.requiresAgeVerification());
        assertFalse(regular.requiresAgeVerification());
    }
}
