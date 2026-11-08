package com.freebie.restaurantlisting.exception;

import java.util.UUID;

public class RestaurantOrderNotFoundException extends RuntimeException {
    public RestaurantOrderNotFoundException(UUID id) { super("Order not found: " + id); }
}
