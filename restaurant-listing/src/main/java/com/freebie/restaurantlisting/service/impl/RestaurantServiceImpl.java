package com.freebie.restaurantlisting.service.impl;

import com.freebie.restaurantlisting.dto.CreateRestaurantRequest;
import com.freebie.restaurantlisting.dto.RestaurantResponse;
import com.freebie.restaurantlisting.dto.UpdateRestaurantRequest;
import com.freebie.restaurantlisting.entity.Restaurant;
import com.freebie.restaurantlisting.exception.RestaurantNotFoundException;
import com.freebie.restaurantlisting.geocoding.Coordinates;
import com.freebie.restaurantlisting.geocoding.GeocodingService;
import com.freebie.restaurantlisting.mapper.RestaurantMapper;
import com.freebie.restaurantlisting.repository.RestaurantRepository;
import com.freebie.restaurantlisting.service.RestaurantService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository repo;
    private final RestaurantMapper mapper;
    private final GeocodingService geocodingService;

    public RestaurantServiceImpl(RestaurantRepository repo, RestaurantMapper mapper, GeocodingService geocodingService) {
        this.repo = repo;
        this.mapper = mapper;
        this.geocodingService = geocodingService;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public RestaurantResponse createRestaurant(CreateRestaurantRequest req, UUID ownerId) {
        Coordinates coords = req.hasPin()
                ? new Coordinates(req.latitude(), req.longitude())
                : geocodingService.geocode(req.address());
        Restaurant newRestaurant = mapper.toEntity(req, coords, ownerId);
        Restaurant saved = repo.save(newRestaurant);
        return mapper.toResponse(saved);
    }

    @Override
    public RestaurantResponse getRestaurantById(UUID id) {
        return mapper.toResponse(findOrThrow(id));
    }

    @Override
    public List<RestaurantResponse> fetchAllRestaurants(Pageable pageable) {
        return repo.findAll(pageable).map(mapper::toResponse).getContent();
    }

    @Override
    public List<RestaurantResponse> getRestaurantsByOwnerId(UUID ownerId) {
        return repo.findByOwnerId(ownerId).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public RestaurantResponse updateRestaurant(UUID id, UpdateRestaurantRequest req, UUID userId) {
        Restaurant restaurant = findOrThrow(id);
        if (!restaurant.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("You do not own this restaurant");
        }
        mapper.applyUpdate(restaurant, req);
        return mapper.toResponse(restaurant);
    }

    private Restaurant findOrThrow(UUID id) {
        return repo.findById(id).orElseThrow(() -> new RestaurantNotFoundException(id));
    }
}
