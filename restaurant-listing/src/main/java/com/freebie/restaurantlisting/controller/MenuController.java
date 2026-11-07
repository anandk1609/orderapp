package com.freebie.restaurantlisting.controller;

import com.freebie.restaurantlisting.dto.MenuItemRequest;
import com.freebie.restaurantlisting.dto.MenuItemResponse;
import com.freebie.restaurantlisting.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu")
public class MenuController {

    private final MenuService service;

    public MenuController(MenuService service) {
        this.service = service;
    }

    @GetMapping
    public List<MenuItemResponse> getMenuItems(@PathVariable("restaurantId") UUID restaurantId) {
        return service.getMenuItems(restaurantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse getMenuItem(@PathVariable("restaurantId") UUID restaurantId,
                                        @Valid @RequestBody MenuItemRequest request,
                                        @AuthenticationPrincipal Jwt jwt) {
        return service.addMenuItem(restaurantId, request, userId(jwt));
    }

    @PutMapping("/{itemId}")
    public MenuItemResponse updateMenuItem(@PathVariable("restaurantId") UUID restaurantId,
                                           @PathVariable("itemId") UUID itemId,
                                           @Valid @RequestBody MenuItemRequest request,
                                           @AuthenticationPrincipal Jwt jwt) {
        return service.updateMenuItem(restaurantId, itemId, request, userId(jwt));
    }

    @DeleteMapping("/{itemId}")
    public void deleteMenuItem(@PathVariable("restaurantId") UUID restaurantId,
                               @PathVariable("itemId") UUID itemId,
                               @AuthenticationPrincipal Jwt jwt) {
        service.deleteMenuItem(restaurantId, itemId, userId(jwt));
    }

    private UUID userId(Jwt jwt) {
        return UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
    }
}
