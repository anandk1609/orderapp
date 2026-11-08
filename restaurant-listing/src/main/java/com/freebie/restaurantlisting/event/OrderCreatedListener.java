package com.freebie.restaurantlisting.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.freebie.restaurantlisting.service.RestaurantOrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class OrderCreatedListener {

    private final ObjectMapper mapper;
    private final RestaurantOrderService orderService;

    public OrderCreatedListener(ObjectMapper mapper, RestaurantOrderService orderService) {
        this.mapper = mapper;
        this.orderService = orderService;
    }

    @KafkaListener(topics = Topics.ORDER_CREATED)
    public void onMessage(String payload) throws JsonProcessingException {
        orderService.receiveOrder(mapper.readValue(payload, OrderCreatedEvent.class));
    }
}
