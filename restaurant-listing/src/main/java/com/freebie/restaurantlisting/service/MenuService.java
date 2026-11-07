package com.freebie.restaurantlisting.service;

import com.freebie.restaurantlisting.dto.MenuItemRequest;
import com.freebie.restaurantlisting.dto.MenuItemResponse;

import java.util.List;
import java.util.UUID;

public interface MenuService {
    List<MenuItemResponse> getMenuItems(UUID restaurantId);
    MenuItemResponse addMenuItem(UUID restaurantId, MenuItemRequest request, UUID userId);
    List<MenuItemResponse> addMenuItemsInBatch(UUID restaurantId, List<MenuItemRequest> requests, UUID userId);
    MenuItemResponse updateMenuItem(UUID restaurantId, UUID itemId, MenuItemRequest request, UUID userId);
    void deleteMenuItem(UUID restaurantId, UUID itemId, UUID userId);
}
