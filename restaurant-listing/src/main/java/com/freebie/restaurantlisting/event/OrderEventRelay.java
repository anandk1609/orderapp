package com.freebie.restaurantlisting.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.UUID;

@Component
public class OrderEventRelay {

    private static final Logger log = LoggerFactory.getLogger(OrderEventRelay.class);

    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper;

    public OrderEventRelay(KafkaTemplate<String, String> kafka, ObjectMapper mapper) {
        this.kafka = kafka;
        this.mapper = mapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDecision(RestaurantDecisionEvent event) {
        send(Topics.RESTAURANT_DECISION, event.orderId(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReady(OrderReadyEvent event) {
        send(Topics.ORDER_READY, event.orderId(), event);
    }

    private void send(String topic, UUID orderId, Object event) {
        String json = mapper.writeValueAsString(event);
        kafka.send(topic, orderId.toString(), json).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish to {} for order {}", topic, orderId, ex);
            }
        });
    }
}