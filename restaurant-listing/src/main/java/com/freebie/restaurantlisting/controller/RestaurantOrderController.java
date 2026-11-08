package com.freebie.restaurantlisting.controller;

import com.freebie.restaurantlisting.dto.RejectOrderRequest;
import com.freebie.restaurantlisting.dto.RestaurantOrderResponse;
import com.freebie.restaurantlisting.event.OrderStatus;
import com.freebie.restaurantlisting.service.RestaurantOrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/orders")
public class RestaurantOrderController {

    private final RestaurantOrderService service;

    public RestaurantOrderController(RestaurantOrderService service) {
        this.service = service;
    }

    @GetMapping   // ?status=PENDING&page=0&size=20
    public List<RestaurantOrderResponse> list(
            @PathVariable UUID restaurantId,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        return service.listOrders(restaurantId, status, userId(jwt), pageable);
    }

    @PostMapping("/{orderId}/accept")
    public RestaurantOrderResponse accept(@PathVariable UUID restaurantId, @PathVariable UUID orderId,
                                          @AuthenticationPrincipal Jwt jwt) {
        return service.accept(restaurantId, orderId, userId(jwt));
    }

    @PostMapping("/{orderId}/reject")
    public RestaurantOrderResponse reject(@PathVariable UUID restaurantId, @PathVariable UUID orderId,
                                          @Valid @RequestBody RejectOrderRequest request,
                                          @AuthenticationPrincipal Jwt jwt) {
        return service.reject(restaurantId, orderId, request, userId(jwt));
    }

    @PostMapping("/{orderId}/prepared")
    public RestaurantOrderResponse prepared(@PathVariable UUID restaurantId, @PathVariable UUID orderId,
                                            @AuthenticationPrincipal Jwt jwt) {
        return service.markPrepared(restaurantId, orderId, userId(jwt));
    }

    private UUID userId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
