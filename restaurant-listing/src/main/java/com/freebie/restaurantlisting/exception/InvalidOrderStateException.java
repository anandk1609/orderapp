package com.freebie.restaurantlisting.exception;

import com.freebie.restaurantlisting.event.OrderStatus;

import java.util.UUID;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(UUID id, OrderStatus status, String action) {
        super("Cannot " + action + " order " + id + " while it is " + status);
    }
}
