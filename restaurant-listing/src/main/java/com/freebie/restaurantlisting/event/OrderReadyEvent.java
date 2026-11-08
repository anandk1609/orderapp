package com.freebie.restaurantlisting.event;

import java.time.Instant;
import java.util.UUID;

public record OrderReadyEvent(
      UUID eventId, UUID orderId, UUID restaurantId,
      String pickupAddress, double pickupLatitude, double pickupLongitude, Instant occuredAt) {}
