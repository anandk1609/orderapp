package com.freebie.restaurantlisting.dto;

import com.freebie.restaurantlisting.event.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RestaurantOrderResponse(UUID id, UUID restaurantId, UUID customerId, BigDecimal total,
                                      OrderStatus status, String rejectionReason, List<Line> items,
                                      Instant createdAt, Instant updatedAt) {
    public record Line(String name, int quantity) {}
}
