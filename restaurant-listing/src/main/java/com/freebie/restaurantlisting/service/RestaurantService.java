package com.freebie.restaurantlisting.service;

import com.freebie.restaurantlisting.dto.CreateRestaurantRequest;
import com.freebie.restaurantlisting.dto.RestaurantResponse;
import com.freebie.restaurantlisting.dto.UpdateRestaurantRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RestaurantService {
    RestaurantResponse createRestaurant(CreateRestaurantRequest req, UUID ownerId);
    RestaurantResponse getRestaurantById(UUID id);
    List<RestaurantResponse> fetchAllRestaurants(Pageable pageable);
    List<RestaurantResponse> getRestaurantsByOwnerId(UUID ownerId);
    RestaurantResponse updateRestaurant(UUID id, UpdateRestaurantRequest req, UUID userId);
}
