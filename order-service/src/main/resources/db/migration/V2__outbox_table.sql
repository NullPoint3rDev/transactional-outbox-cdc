CREATE TABLE outbox (
    outbox_id   BIGSERIAL PRIMARY KEY,
    aggregate_type varchar(25),
    event_type  varchar(50),
    payload text,
    created_at  timestamp
)