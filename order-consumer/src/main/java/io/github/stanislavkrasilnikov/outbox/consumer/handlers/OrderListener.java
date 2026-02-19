package io.github.stanislavkrasilnikov.outbox.consumer.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.stanislavkrasilnikov.outbox.consumer.envelope.DebeziumEnvelope;
import io.github.stanislavkrasilnikov.outbox.events.OrderCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {

    private final ObjectMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(OrderListener.class);

    public OrderListener(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @KafkaListener(topics = "dbserver.public.outbox", groupId = "order-consumer-group")
    public void handleOutboxEvent(String value) {
        try {
            DebeziumEnvelope envelope = mapper.readValue(value, DebeziumEnvelope.class);
            if(envelope.getAfter() == null) return;

            String payloadJson = (String) envelope.getAfter().get("payload");
            if(payloadJson == null || payloadJson.isBlank()) return;

            OrderCreated event = mapper.readValue(payloadJson, OrderCreated.class);
            processOrderCreated(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void processOrderCreated(OrderCreated event) {
        log.info("Order created: orderId={}, customerId={}", event.orderId(), event.customerId());
    }
}
