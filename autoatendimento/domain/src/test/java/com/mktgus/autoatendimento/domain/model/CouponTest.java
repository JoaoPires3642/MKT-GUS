package com.mktgus.autoatendimento.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    private Coupon percentageCoupon(double pct, Double minPurchase, Double maxDiscount) {
        return new Coupon(1L, "PCT", "desc", pct, true, 0, null, minPurchase, maxDiscount);
    }

    private Coupon fixedCoupon(double value, Double minPurchase) {
        return new Coupon(2L, "FIXED", "desc", value, false, 0, null, minPurchase, null);
    }

    // --- isEligibleForPurchase ---

    @Test
    void eligibleWhenNoPurchaseMinimum() {
        Coupon coupon = percentageCoupon(10.0, null, null);
        assertTrue(coupon.isEligibleForPurchase(0.01));
        assertTrue(coupon.isEligibleForPurchase(1000.0));
    }

    @Test
    void eligibleWhenPurchaseMeetsMinimum() {
        Coupon coupon = fixedCoupon(5.0, 50.0);
        assertTrue(coupon.isEligibleForPurchase(50.0));
        assertTrue(coupon.isEligibleForPurchase(100.0));
    }

    @Test
    void notEligibleWhenPurchaseBelowMinimum() {
        Coupon coupon = fixedCoupon(5.0, 50.0);
        assertFalse(coupon.isEligibleForPurchase(49.99));
    }

    // --- calculateDiscount ---

    @Test
    void percentageDiscountCalculatedCorrectly() {
        Coupon coupon = percentageCoupon(20.0, null, null);
        assertEquals(20.0, coupon.calculateDiscount(100.0), 0.001);
    }

    @Test
    void percentageDiscountCappedByMaximum() {
        Coupon coupon = percentageCoupon(50.0, null, 30.0);
        assertEquals(30.0, coupon.calculateDiscount(100.0), 0.001);
    }

    @Test
    void percentageDiscountBelowMaxIsNotCapped() {
        Coupon coupon = percentageCoupon(10.0, null, 30.0);
        assertEquals(10.0, coupon.calculateDiscount(100.0), 0.001);
    }

    @Test
    void fixedDiscountReturnsDiscountValue() {
        Coupon coupon = fixedCoupon(15.0, null);
        assertEquals(15.0, coupon.calculateDiscount(100.0), 0.001);
        assertEquals(15.0, coupon.calculateDiscount(20.0), 0.001);
    }

    // --- applyTo ---

    @Test
    void applyToShouldSubtractDiscount() {
        Coupon coupon = fixedCoupon(20.0, null);
        assertEquals(80.0, coupon.applyTo(100.0), 0.001);
    }

    @Test
    void applyToShouldNeverGoBelowZero() {
        Coupon coupon = fixedCoupon(200.0, null);
        assertEquals(0.0, coupon.applyTo(100.0), 0.001);
    }

    @Test
    void applyToWithPercentageCouponAndCap() {
        Coupon coupon = percentageCoupon(50.0, null, 30.0);
        // discount capped at 30, so result = 100 - 30 = 70
        assertEquals(70.0, coupon.applyTo(100.0), 0.001);
    }
}
