package com.stockpilot.common.idempotency.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "idempotency_records",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_idempotency_key",
                columnNames = "idempotency_key"
        )
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "idempotency_key",
            nullable = false,
            updatable = false
    )
    private UUID idempotencyKey;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false, length = 64)
    private String requestHash;

    @Column(nullable = false)
    private Long movementId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}