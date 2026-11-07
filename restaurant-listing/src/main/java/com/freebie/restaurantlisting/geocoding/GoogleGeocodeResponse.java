package com.freebie.restaurantlisting.geocoding;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleGeocodeResponse(
        String status,
        @JsonProperty("error_message") String errorMessage,
        List<Result> results
) {
    public record Result(Geometry geometry) {}
    public record Geometry(Location location) {}
    public record Location(double lat, double lng) {}
}
