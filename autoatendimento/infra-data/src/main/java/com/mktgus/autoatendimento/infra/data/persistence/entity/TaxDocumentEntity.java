package com.mktgus.autoatendimento.infra.data.persistence.entity;

import com.mktgus.autoatendimento.domain.model.TaxDocumentStatus;
import com.mktgus.autoatendimento.domain.model.TaxDocumentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidade JPA para persistência do documento fiscal.
 *
 * DDL esperado:
 *
 * CREATE TABLE tax_document (
 *   id                  BIGSERIAL PRIMARY KEY,
 *   order_id            BIGINT NOT NULL,
 *   status              VARCHAR(20) NOT NULL,
 *   type                VARCHAR(10) NOT NULL,
 *   document_number     VARCHAR(20),
 *   access_key          VARCHAR(44),
 *   danfe_url           TEXT,
 *   failure_reason      TEXT,
 *   attempts            INT NOT NULL DEFAULT 0,
 *   issued_at           TIMESTAMP,
 *   last_attempt_at     TIMESTAMP
 * );
 */
@Entity
@Table(name = "tax_document")
public class TaxDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaxDocumentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TaxDocumentType type;

    @Column(name = "document_number", length = 20)
    private String documentNumber;

    @Column(name = "access_key", length = 44)
    private String accessKey;

    @Column(name = "danfe_url", columnDefinition = "TEXT")
    private String danfeUrl;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    public TaxDocumentEntity() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public TaxDocumentStatus getStatus() { return status; }
    public void setStatus(TaxDocumentStatus status) { this.status = status; }

    public TaxDocumentType getType() { return type; }
    public void setType(TaxDocumentType type) { this.type = type; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

    public String getDanfeUrl() { return danfeUrl; }
    public void setDanfeUrl(String danfeUrl) { this.danfeUrl = danfeUrl; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getLastAttemptAt() { return lastAttemptAt; }
    public void setLastAttemptAt(LocalDateTime lastAttemptAt) { this.lastAttemptAt = lastAttemptAt; }
}