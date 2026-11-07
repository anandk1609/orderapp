package com.freebie.restaurantlisting.exception;

import jakarta.validation.constraints.NotBlank;

public class DuplicateMenuItemException extends RuntimeException {
    public DuplicateMenuItemException(@NotBlank String s) {
        super(s);
    }
}
