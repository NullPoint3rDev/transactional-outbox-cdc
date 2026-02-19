package io.github.stanislavkrasilnikov.outbox.order.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    private UUID orderId;

    private String customerId;

    private BigDecimal totalPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
