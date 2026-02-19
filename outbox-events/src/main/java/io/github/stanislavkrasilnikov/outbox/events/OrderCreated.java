package io.github.stanislavkrasilnikov.outbox.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreated(
   UUID orderId,
   String customerId,
   BigDecimal totalPrice,
   List<Item> items,
   Instant createdAt
) {}
