package com.freebie.restaurantlisting.exception;

import java.util.UUID;

public class MenuItemNotFoundException extends RuntimeException {
    public MenuItemNotFoundException(UUID itemId) {
        super("Item with id " + itemId + " not found");
    }
}
