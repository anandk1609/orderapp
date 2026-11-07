package com.freebie.restaurantlisting.exception;

public class GeocodingUnavailableException extends RuntimeException {
    public GeocodingUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
