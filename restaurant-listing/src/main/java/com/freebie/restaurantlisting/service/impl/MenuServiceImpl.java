package com.freebie.restaurantlisting.service.impl;

import com.freebie.restaurantlisting.dto.MenuItemRequest;
import com.freebie.restaurantlisting.dto.MenuItemResponse;
import com.freebie.restaurantlisting.entity.MenuItem;
import com.freebie.restaurantlisting.entity.Restaurant;
import com.freebie.restaurantlisting.exception.DuplicateMenuItemException;
import com.freebie.restaurantlisting.exception.MenuItemNotFoundException;
import com.freebie.restaurantlisting.exception.RestaurantNotFoundException;
import com.freebie.restaurantlisting.mapper.MenuItemMapper;
import com.freebie.restaurantlisting.repository.MenuItemRepository;
import com.freebie.restaurantlisting.repository.RestaurantRepository;
import com.freebie.restaurantlisting.service.MenuService;
import com.freebie.restaurantlisting.utils.RestaurantOwnership;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {
    private final MenuItemRepository items;
    private final RestaurantRepository restaurant;
    private final RestaurantOwnership owner;
    private final MenuItemMapper mapper;

    public MenuServiceImpl(MenuItemRepository items, RestaurantRepository restaurant, RestaurantOwnership owner, MenuItemMapper mapper) {
        this.items = items;
        this.restaurant = restaurant;
        this.owner = owner;
        this.mapper = mapper;
    }

    @Override
    public List<MenuItemResponse> getMenuItems(UUID restaurantId) {
        if (!restaurant.existsById(restaurantId)) {
            throw new RestaurantNotFoundException(restaurantId);
        }
        return items.findByRestaurantIdOrderByNameAsc(restaurantId).stream()
                .map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public MenuItemResponse addMenuItem(UUID restaurantId, MenuItemRequest request, UUID userId) {
        Restaurant restaurant = owner.requireOwnedRestaurant(restaurantId, userId);
        MenuItem saved = items.save(new MenuItem(restaurant, request.name(), request.description(), request.price()));
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public List<MenuItemResponse> addMenuItemsInBatch(UUID restaurantId, List<MenuItemRequest> requests, UUID userId) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Menu item list must not be empty");
        }
        Restaurant restaurant = owner.requireOwnedRestaurant(restaurantId, userId);
        if (restaurant == null) {
            throw new RestaurantNotFoundException(restaurantId);
        }

        Set<String> existingItems = new HashSet<>();
        for (MenuItemRequest req : requests) {
            String key = req.name().trim().toLowerCase();
            if (!existingItems.add(key)) {
                throw new DuplicateMenuItemException("Duplicate menu item name in request: " + req.name());
            }
            if (items.existsByRestaurantIdAndNameIgnoreCase(restaurantId, key)) {
                throw new DuplicateMenuItemException(
                        "Menu item already exists: " + req.name());
            }
        }

        List<MenuItem> entities = requests.stream()
                .map(req -> new MenuItem(restaurant, req.name(), req.description(), req.price()))
                .toList();

        List<MenuItem> saved = items.saveAll(entities);
        return saved.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(UUID restaurantId, UUID itemId, MenuItemRequest request, UUID userId) {
        owner.requireOwnedRestaurant(restaurantId, userId);
        MenuItem item = findOrThrow(itemId, restaurantId);
        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());  //saved by dirty checking at commit
        return mapper.toResponse(item);
    }

    @Override
    @Transactional
    public void deleteMenuItem(UUID restaurantId, UUID itemId, UUID userId) {
        owner.requireOwnedRestaurant(restaurantId, userId);
        items.delete(findOrThrow(itemId, restaurantId));
    }

    private MenuItem findOrThrow(UUID itemId, UUID restaurantId) {
        return items.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(() -> new MenuItemNotFoundException(itemId));
    }
}
