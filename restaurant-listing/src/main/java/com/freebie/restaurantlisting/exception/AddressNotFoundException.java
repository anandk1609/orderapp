package com.freebie.restaurantlisting.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String address) {
        super("Could not find a location for the address: " + address);
    }
}
