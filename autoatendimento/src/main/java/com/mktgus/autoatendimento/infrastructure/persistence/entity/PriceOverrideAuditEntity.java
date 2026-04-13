package com.mktgus.autoatendimento.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class PriceOverrideAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, length = 64)
    private String ean;

    @Column(nullable = false, length = 255)
    private String productName;

    @Column(nullable = false)
    private double originalUnitPrice;

    @Column(nullable = false)
    private double authorizedUnitPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Long employeeRegistration;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime authorizedAt;

    public PriceOverrideAuditEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getOriginalUnitPrice() {
        return originalUnitPrice;
    }

    public void setOriginalUnitPrice(double originalUnitPrice) {
        this.originalUnitPrice = originalUnitPrice;
    }

    public double getAuthorizedUnitPrice() {
        return authorizedUnitPrice;
    }

    public void setAuthorizedUnitPrice(double authorizedUnitPrice) {
        this.authorizedUnitPrice = authorizedUnitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getEmployeeRegistration() {
        return employeeRegistration;
    }

    public void setEmployeeRegistration(Long employeeRegistration) {
        this.employeeRegistration = employeeRegistration;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getAuthorizedAt() {
        return authorizedAt;
    }

    public void setAuthorizedAt(LocalDateTime authorizedAt) {
        this.authorizedAt = authorizedAt;
    }
}
