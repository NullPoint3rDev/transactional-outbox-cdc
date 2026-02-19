package io.github.stanislavkrasilnikov.outbox.order.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.stanislavkrasilnikov.outbox.events.Item;
import io.github.stanislavkrasilnikov.outbox.events.OrderCreated;
import io.github.stanislavkrasilnikov.outbox.order.domain.Order;
import io.github.stanislavkrasilnikov.outbox.order.domain.OutboxEvent;
import io.github.stanislavkrasilnikov.outbox.order.repositories.OrderRepository;
import io.github.stanislavkrasilnikov.outbox.order.repositories.OutboxRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static io.github.stanislavkrasilnikov.outbox.events.Constants.EVENT_TYPE_ORDER_CREATED;

@Component
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper mapper;

    public OrderService(OrderRepository orderRepository, OutboxRepository outboxRepository, ObjectMapper mapper) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Order createOrder(String customerId, BigDecimal totalPrice, List<Item> items) {
        UUID orderId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        Order order = new Order();
        order.setOrderId(orderId);
        order.setCustomerId(customerId);
        order.setTotalPrice(totalPrice);
        order.setCreatedAt(createdAt);
        orderRepository.save(order);

        OrderCreated orderCreated = new OrderCreated(
                orderId,
                customerId,
                totalPrice,
                items,
                createdAt
        );

        try {
            String payloadJson = mapper.writeValueAsString(orderCreated);

            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("order");
            outboxEvent.setEventType(EVENT_TYPE_ORDER_CREATED);
            outboxEvent.setPayload(payloadJson);
            outboxEvent.setCreatedAt(createdAt);
            outboxRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return order;
    }
}
