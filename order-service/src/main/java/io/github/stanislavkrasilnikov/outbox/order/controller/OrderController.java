package io.github.stanislavkrasilnikov.outbox.order.controller;

import io.github.stanislavkrasilnikov.outbox.order.domain.Order;
import io.github.stanislavkrasilnikov.outbox.order.dto.CreateOrderRequest;
import io.github.stanislavkrasilnikov.outbox.order.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        Order created =
                orderService.createOrder(request.getCustomerId(), request.getTotalPrice(), request.getItems());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
