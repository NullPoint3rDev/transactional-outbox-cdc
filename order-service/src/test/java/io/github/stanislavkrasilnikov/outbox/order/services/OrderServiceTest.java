package io.github.stanislavkrasilnikov.outbox.order.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.stanislavkrasilnikov.outbox.events.Item;
import io.github.stanislavkrasilnikov.outbox.order.domain.Order;
import io.github.stanislavkrasilnikov.outbox.order.domain.OutboxEvent;
import io.github.stanislavkrasilnikov.outbox.order.repositories.OrderRepository;
import io.github.stanislavkrasilnikov.outbox.order.repositories.OutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static io.github.stanislavkrasilnikov.outbox.events.Constants.EVENT_TYPE_ORDER_CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutboxRepository outboxRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, outboxRepository, objectMapper);
    }

    @Test
    void createOrder_savesOrderAndOutboxInTransaction() {
        String customerId = "cust-1";
        BigDecimal totalPrice = new BigDecimal("99.99");
        List<Item> items = List.of(
                new Item("item-1", 2, new BigDecimal("49.99"))
        );

        Order order = orderService.createOrder(customerId, totalPrice, items);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getOrderId()).isNotNull();
        assertThat(savedOrder.getCustomerId()).isEqualTo(customerId);
        assertThat(savedOrder.getTotalPrice()).isEqualByComparingTo(totalPrice);
        assertThat(savedOrder.getCreatedAt()).isNotNull();

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxRepository).save(outboxCaptor.capture());
        OutboxEvent savedOutbox = outboxCaptor.getValue();
        assertThat(savedOutbox.getAggregateType()).isEqualTo("order");
        assertThat(savedOutbox.getEventType()).isEqualTo(EVENT_TYPE_ORDER_CREATED);
        assertThat(savedOutbox.getPayload()).isNotBlank().contains(customerId);
        assertThat(savedOutbox.getCreatedAt()).isNotNull();

        assertThat(order).isSameAs(savedOrder);
    }
}
