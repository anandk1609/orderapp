package com.freebie.restaurantlisting.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantRequest(
        @NotNull @Size(max = 120) String name,
        @NotNull Boolean open
) {}
