package com.freebie.restaurantlisting.geocoding;

import java.util.Locale;

public final class AddressNormalizer {

    private AddressNormalizer() {}

    public static String normalize(String address) {
        return address.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[,.;]", " ")     // punctuation becomes a space
                .replaceAll("\\s+", " ");     // collapse repeated spaces
    }
}
