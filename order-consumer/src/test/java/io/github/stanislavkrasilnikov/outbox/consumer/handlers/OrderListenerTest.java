package io.github.stanislavkrasilnikov.outbox.consumer.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.stanislavkrasilnikov.outbox.events.OrderCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderListenerTest {

    private ObjectMapper objectMapper;
    private OrderListener listener;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        listener = new OrderListener(objectMapper);
    }

    @Test
    void handleOutboxEvent_parsesEnvelopeAndCallsProcessOrderCreated() throws Exception {
        UUID orderId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        OrderCreated orderCreated = new OrderCreated(
                orderId,
                "cust-1",
                new BigDecimal("99.99"),
                List.of(),
                createdAt
        );
        String payloadJson = objectMapper.writeValueAsString(orderCreated);

        Map<String, Object> after = Map.of(
                "outbox_id", 1L,
                "aggregate_type", "order",
                "event_type", "ORDER_CREATED",
                "payload", payloadJson,
                "created_at", createdAt.toString()
        );
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("before", null);
        envelope.put("after", after);
        envelope.put("source", Map.of("name", "dbserver"));
        String envelopeJson = objectMapper.writeValueAsString(envelope);

        OrderListener spy = org.mockito.Mockito.spy(listener);
        doNothing().when(spy).processOrderCreated(any(OrderCreated.class));

        spy.handleOutboxEvent(envelopeJson);

        ArgumentCaptor<OrderCreated> captor = ArgumentCaptor.forClass(OrderCreated.class);
        verify(spy).processOrderCreated(captor.capture());
        OrderCreated captured = captor.getValue();
        assertThat(captured.orderId()).isEqualTo(orderId);
        assertThat(captured.customerId()).isEqualTo("cust-1");
        assertThat(captured.totalPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
    }

    @Test
    void handleOutboxEvent_skipsWhenAfterIsNull() throws Exception {
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("before", null);
        envelope.put("after", null);
        envelope.put("source", Map.of());
        String envelopeJson = objectMapper.writeValueAsString(envelope);

        OrderListener spy = org.mockito.Mockito.spy(listener);
        spy.handleOutboxEvent(envelopeJson);

        verify(spy, never()).processOrderCreated(any(OrderCreated.class));
    }

    @Test
    void handleOutboxEvent_skipsWhenPayloadIsBlank() throws Exception {
        Map<String, Object> after = Map.of(
                "outbox_id", 1L,
                "payload", "",
                "event_type", "ORDER_CREATED"
        );
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("before", null);
        envelope.put("after", after);
        envelope.put("source", Map.of());
        String envelopeJson = objectMapper.writeValueAsString(envelope);

        OrderListener spy = org.mockito.Mockito.spy(listener);
        spy.handleOutboxEvent(envelopeJson);

        verify(spy, never()).processOrderCreated(any(OrderCreated.class));
    }
}
