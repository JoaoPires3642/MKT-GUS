package com.mktgus.autoatendimento.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductWithValidData() {
        Product product = new Product("7891234567890", "Café Solúvel", null, 15.90, false);
        assertEquals("7891234567890", product.ean());
        assertEquals(15.90, product.price());
        assertFalse(product.adultOnly());
    }

    @Test
    void shouldRejectBlankEan() {
        assertThrows(IllegalArgumentException.class, () -> new Product("", "Nome", null, 10.0, false));
    }

    @Test
    void shouldRejectNullEan() {
        assertThrows(IllegalArgumentException.class, () -> new Product(null, "Nome", null, 10.0, false));
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new Product("123", " ", null, 10.0, false));
    }

    @Test
    void shouldRejectNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> new Product("123", "Nome", null, -1.0, false));
    }

    @Test
    void requiresAgeVerificationShouldReflectAdultOnlyFlag() {
        Product adult = new Product("123", "Cerveja", null, 8.0, true);
        Product regular = new Product("456", "Suco", null, 5.0, false);
        assertTrue(adult.requiresAgeVerification());
        assertFalse(regular.requiresAgeVerification());
    }

    @Test
    void hasPriceDivergenceShouldDetectDifferencesAboveThreshold() {
        Product product = new Product("123", "Pão", null, 10.0, false);
        assertTrue(product.hasPriceDivergence(9.0));
        assertTrue(product.hasPriceDivergence(11.0));
    }

    @Test
    void hasPriceDivergenceShouldIgnoreTinyFloatingPointDiff() {
        Product product = new Product("123", "Pão", null, 10.0, false);
        assertFalse(product.hasPriceDivergence(10.005));
        assertFalse(product.hasPriceDivergence(10.0));
    }
}
