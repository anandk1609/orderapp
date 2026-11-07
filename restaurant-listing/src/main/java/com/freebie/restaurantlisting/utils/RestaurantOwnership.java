package com.freebie.restaurantlisting.utils;

import com.freebie.restaurantlisting.entity.Restaurant;
import com.freebie.restaurantlisting.exception.RestaurantNotFoundException;
import com.freebie.restaurantlisting.repository.RestaurantRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestaurantOwnership {

    private final RestaurantRepository repository;

    public RestaurantOwnership(RestaurantRepository repository) {
        this.repository = repository;
    }

    public Restaurant requireOwnedRestaurant(UUID restaurantId, UUID userId) {
        Restaurant restaurant = repository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        if (!restaurant.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("You are not the owner of this restaurant");
        }
        return restaurant;
    }
}
