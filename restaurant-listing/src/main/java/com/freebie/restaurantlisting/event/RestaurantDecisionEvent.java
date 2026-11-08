package com.freebie.restaurantlisting.event;

import java.time.Instant;
import java.util.UUID;

public record RestaurantDecisionEvent(
        UUID eventId, UUID orderId, UUID restaurantId,
        Decision decision, String reason, Instant occuredAt) {

    public enum Decision { ACCEPTED, REJECTED }

    public static RestaurantDecisionEvent accepted(UUID orderId, UUID restaurantId) {
        return new RestaurantDecisionEvent(UUID.randomUUID(), orderId, restaurantId,
                Decision.ACCEPTED, null, Instant.now());
    }

    public static RestaurantDecisionEvent rejected(UUID orderId, UUID restaurantId, String reason) {
        return new RestaurantDecisionEvent(UUID.randomUUID(), orderId, restaurantId,
                Decision.REJECTED, reason, Instant.now());
    }
}
