package io.github.stanislavkrasilnikov.outbox.events;

import java.math.BigDecimal;

public record Item(
   String itemId,
   int itemsAmount,
   BigDecimal itemsPrice
) {}
