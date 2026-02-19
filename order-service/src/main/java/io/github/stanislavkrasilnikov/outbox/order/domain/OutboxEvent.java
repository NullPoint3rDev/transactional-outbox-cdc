package io.github.stanislavkrasilnikov.outbox.order.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "outbox")
@Data
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outboxId;

    private String aggregateType;

    private String eventType;

    @Column(name = "payload", updatable = false)
    private String payload;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
