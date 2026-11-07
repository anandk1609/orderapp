package com.freebie.restaurantlisting.controller;

import com.freebie.restaurantlisting.dto.CreateRestaurantRequest;
import com.freebie.restaurantlisting.dto.RestaurantResponse;
import com.freebie.restaurantlisting.dto.UpdateRestaurantRequest;
import com.freebie.restaurantlisting.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService service;

    @Autowired
    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(@Valid @RequestBody CreateRestaurantRequest req,
                                     @AuthenticationPrincipal Jwt jwt) {
        return service.createRestaurant(req, userId(jwt));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RestaurantResponse> listRestaurants(Pageable pageable) {
        return service.fetchAllRestaurants(pageable);
    }

    @GetMapping("/mine")
    public List<RestaurantResponse> mine(@AuthenticationPrincipal Jwt jwt) {
        return service.getRestaurantsByOwnerId(userId(jwt));
    }

    @GetMapping("/{id}")
    public RestaurantResponse get(@PathVariable UUID id) {
        return service.getRestaurantById(id);
    }

    @PutMapping("/{id}")
    public RestaurantResponse update(@PathVariable UUID id,
                                     @Valid @RequestBody UpdateRestaurantRequest request,
                                     @AuthenticationPrincipal Jwt jwt) {
        return service.updateRestaurant(id, request, userId(jwt));
    }

    private UUID userId(Jwt jwt) {
        return UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
    }
}
