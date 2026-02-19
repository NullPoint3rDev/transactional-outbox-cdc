package io.github.stanislavkrasilnikov.outbox.consumer.envelope;

import lombok.Data;

import java.util.Map;

@Data
public class DebeziumEnvelope {
    private Object before;
    private Map<String, Object> after;
    private Map<String, Object> source;
}
