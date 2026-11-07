package com.freebie.restaurantlisting.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        UUID ownerId,
        String name,
        String address,
        double latitude,
        double longitude,
        boolean open,
        BigDecimal ratingAvg,
        int ratingCount,
        Instant createdAt
) {}
