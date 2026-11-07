package com.freebie.restaurantlisting.geocoding;

public interface GeocodingService {
    Coordinates geocode(String address);
}
