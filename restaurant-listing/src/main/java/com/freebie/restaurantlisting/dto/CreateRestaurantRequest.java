package com.freebie.restaurantlisting.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

public record CreateRestaurantRequest(
        @NotNull @Size(max = 120) String name,
        @NotNull @Size(max = 255) String address,
        @DecimalMin("-90.0")  @DecimalMax("90.0") Double latitude,
        @DecimalMin("-180.0")  @DecimalMax("180.0") Double longitude
) {

    @JsonIgnore
    @AssertTrue(message = "latitude and longitude must be sent together, or both left out")
    public boolean isLocationPairValid() {
        return (latitude == null ) == (longitude == null);
    }

    @JsonIgnore
    public boolean hasPin() {
        return latitude != null && longitude != null;
    }
}
