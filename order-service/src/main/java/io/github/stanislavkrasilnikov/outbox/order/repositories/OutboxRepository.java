package io.github.stanislavkrasilnikov.outbox.order.repositories;

import io.github.stanislavkrasilnikov.outbox.order.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
}
