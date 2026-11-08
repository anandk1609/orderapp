package com.freebie.restaurantlisting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectOrderRequest(@NotBlank @Size(max = 255) String reason) {
}
