package com.freebie.restaurantlisting.mapper;

import com.freebie.restaurantlisting.geocoding.Coordinates;
import org.springframework.stereotype.Component;
import com.freebie.restaurantlisting.entity.Restaurant;
import com.freebie.restaurantlisting.dto.CreateRestaurantRequest;
import com.freebie.restaurantlisting.dto.UpdateRestaurantRequest;
import com.freebie.restaurantlisting.dto.RestaurantResponse;

import java.util.UUID;

@Component
public class RestaurantMapper {

    public Restaurant toEntity(CreateRestaurantRequest req, Coordinates coords, UUID ownerId) {
        return new Restaurant(ownerId, req.name(), req.address(), coords.latitude(), coords.longitude());
    }

    public void applyUpdate(Restaurant r, UpdateRestaurantRequest req) {
        r.setName(req.name());
        r.setOpen(req.open());
    }

    public RestaurantResponse toResponse(Restaurant r) {
        return new RestaurantResponse(
                r.getId(), r.getOwnerId(), r.getName(), r.getAddress(),
                r.getLatitude(), r.getLongitude(), r.isOpen(), r.getRatingAvg(), r.getRatingCount(),
                r.getCreatedAt()
        );
    }
}
