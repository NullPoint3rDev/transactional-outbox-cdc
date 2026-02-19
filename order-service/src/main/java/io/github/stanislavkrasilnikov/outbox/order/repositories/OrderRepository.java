package io.github.stanislavkrasilnikov.outbox.order.repositories;

import io.github.stanislavkrasilnikov.outbox.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
