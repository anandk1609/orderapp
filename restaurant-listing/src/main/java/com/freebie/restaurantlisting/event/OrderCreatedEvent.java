package com.freebie.restaurantlisting.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

public record OrderCreatedEvent(
        UUID eventId, UUID orderId, UUID restaurantId, UUID customerId,
        BigDecimal total, List<Item> items, Instant occuredAt) {
    public record Item(UUID menuItemId, String name, int quantity, BigDecimal unitPrice) {}
}
