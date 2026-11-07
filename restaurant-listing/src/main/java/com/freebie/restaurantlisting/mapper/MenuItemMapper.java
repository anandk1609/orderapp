package com.freebie.restaurantlisting.mapper;

import com.freebie.restaurantlisting.dto.MenuItemResponse;
import com.freebie.restaurantlisting.entity.MenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {
    public MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(item.getId(), item.getRestaurant().getId(),
                item.getName(), item.getDescription(), item.getPrice());
    }
}
