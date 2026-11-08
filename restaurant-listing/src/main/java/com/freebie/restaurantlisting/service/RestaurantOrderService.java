package com.freebie.restaurantlisting.service;

import com.freebie.restaurantlisting.dto.RejectOrderRequest;
import com.freebie.restaurantlisting.dto.RestaurantOrderResponse;
import com.freebie.restaurantlisting.event.OrderCreatedEvent;
import com.freebie.restaurantlisting.event.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RestaurantOrderService {
    void receiveOrder(OrderCreatedEvent event);
    List<RestaurantOrderResponse> listOrders(UUID restaurantId, OrderStatus status, UUID userId, Pageable pageable);
    RestaurantOrderResponse accept(UUID restaurantId, UUID orderId, UUID userId);
    RestaurantOrderResponse reject(UUID restaurantId, UUID orderId, RejectOrderRequest request, UUID userId);
    RestaurantOrderResponse markPrepared(UUID restaurantId, UUID orderId, UUID userId);
}
