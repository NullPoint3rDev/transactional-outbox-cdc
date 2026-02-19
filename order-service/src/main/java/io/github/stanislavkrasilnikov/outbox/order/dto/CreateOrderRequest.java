package io.github.stanislavkrasilnikov.outbox.order.dto;

import io.github.stanislavkrasilnikov.outbox.events.Item;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
    private String customerId;
    private BigDecimal totalPrice;
    private List<Item> items;
}
