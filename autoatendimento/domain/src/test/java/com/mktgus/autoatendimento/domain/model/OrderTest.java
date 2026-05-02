package com.mktgus.autoatendimento.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    void isAnonymousShouldBeTrueWhenCpfIsNull() {
        Order order = new Order(1L, null, null, null, NOW, 0.0, List.of());
        assertTrue(order.isAnonymous());
    }

    @Test
    void isAnonymousShouldBeFalseWhenCpfIsPresent() {
        Order order = new Order(1L, null, 52998224725L, null, NOW, 0.0, List.of());
        assertFalse(order.isAnonymous());
    }

    @Test
    void itemCountShouldReturnCorrectSize() {
        var items = List.of(
                OrderItem.of("001", "Pão", 2.0, 1, false),
                OrderItem.of("002", "Leite", 5.0, 2, false)
        );
        Order order = new Order(1L, null, null, null, NOW, 0.0, items);
        assertEquals(2, order.itemCount());
    }

    @Test
    void shouldRejectNullItems() {
        assertThrows(NullPointerException.class, () -> new Order(1L, null, null, null, NOW, 0.0, null));
    }

    @Test
    void calculateSubtotalShouldSumTotalPrices() {
        var items = List.of(
                OrderItem.of("001", "Pão", 2.0, 3, false),   // 6.0
                OrderItem.of("002", "Leite", 5.0, 2, false)  // 10.0
        );
        Order order = new Order(1L, null, null, null, NOW, 0.0, items);
        assertEquals(16.0, order.calculateSubtotal(), 0.001);
    }

    @Test
    void calculateSubtotalShouldReturnZeroForEmptyItems() {
        Order order = new Order(1L, null, null, null, NOW, 0.0, List.of());
        assertEquals(0.0, order.calculateSubtotal(), 0.001);
    }

    @Test
    void hasCouponShouldBeTrueWhenCouponIdIsPresent() {
        Order order = new Order(1L, null, null, 42L, NOW, 0.0, List.of());
        assertTrue(order.hasCoupon());
    }

    @Test
    void hasCouponShouldBeFalseWhenCouponIdIsNull() {
        Order order = new Order(1L, null, null, null, NOW, 0.0, List.of());
        assertFalse(order.hasCoupon());
    }
}
