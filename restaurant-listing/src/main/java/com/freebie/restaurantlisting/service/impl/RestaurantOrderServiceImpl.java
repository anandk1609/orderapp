package com.freebie.restaurantlisting.service.impl;

import com.freebie.restaurantlisting.dto.RejectOrderRequest;
import com.freebie.restaurantlisting.dto.RestaurantOrderResponse;
import com.freebie.restaurantlisting.entity.Restaurant;
import com.freebie.restaurantlisting.entity.RestaurantOrder;
import com.freebie.restaurantlisting.event.OrderCreatedEvent;
import com.freebie.restaurantlisting.event.OrderReadyEvent;
import com.freebie.restaurantlisting.event.OrderStatus;
import com.freebie.restaurantlisting.event.RestaurantDecisionEvent;
import com.freebie.restaurantlisting.exception.RestaurantOrderNotFoundException;
import com.freebie.restaurantlisting.mapper.RestaurantOrderMapper;
import com.freebie.restaurantlisting.repository.RestaurantOrderRepository;
import com.freebie.restaurantlisting.repository.RestaurantRepository;
import com.freebie.restaurantlisting.service.RestaurantOrderService;
import com.freebie.restaurantlisting.utils.RestaurantOwnership;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RestaurantOrderServiceImpl implements RestaurantOrderService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantOrderServiceImpl.class);

    private final RestaurantOrderRepository orders;
    private final RestaurantRepository restaurants;
    private final RestaurantOwnership owner;
    private final RestaurantOrderMapper mapper;
    private final ApplicationEventPublisher events;

    public RestaurantOrderServiceImpl(RestaurantOrderRepository orders, RestaurantRepository restaurants, RestaurantOwnership owner, RestaurantOrderMapper mapper, ApplicationEventPublisher events) {
        this.orders = orders;
        this.restaurants = restaurants;
        this.owner = owner;
        this.mapper = mapper;
        this.events = events;
    }

    @Override
    @Transactional
    public void receiveOrder(OrderCreatedEvent event) {
        validate(event);
        if (orders.existsById(event.orderId())) {
            log.info("Duplicate order.created ignored: {}", event.orderId());
            return;
        }
        if (!restaurants.existsById(event.restaurantId())) {
            throw new IllegalArgumentException("Unknown restaurant " + event.restaurantId());
        }
        orders.save(mapper.toEntity(event));
    }

    @Override
    public List<RestaurantOrderResponse> listOrders(UUID restaurantId, OrderStatus status, UUID userId, Pageable pageable) {
        owner.requireOwnedRestaurant(restaurantId, userId);
        Page<RestaurantOrder> page = (status == null)
                ? orders.findByRestaurantId(restaurantId, pageable)
                : orders.findByRestaurantIdAndStatus(restaurantId, status, pageable);
        return page.map(mapper::toResponse).getContent();
    }

    @Override
    @Transactional
    public RestaurantOrderResponse accept(UUID restaurantId, UUID orderId, UUID userId) {
        owner.requireOwnedRestaurant(restaurantId, userId);
        RestaurantOrder order = findOrThrow(orderId, restaurantId);
        if (order.accept()) {
            events.publishEvent(RestaurantDecisionEvent.accepted(orderId, restaurantId));
        }
        return mapper.toResponse(order);
    }

    @Override
    @Transactional
    public RestaurantOrderResponse reject(UUID restaurantId, UUID orderId, RejectOrderRequest request, UUID userId) {
        owner.requireOwnedRestaurant(restaurantId, userId);
        RestaurantOrder order = findOrThrow(orderId, restaurantId);
        if (order.reject(request.reason())) {
            events.publishEvent(RestaurantDecisionEvent.rejected(orderId, restaurantId, request.reason()));
        }
        return mapper.toResponse(order);
    }

    @Override
    @Transactional
    public RestaurantOrderResponse markPrepared(UUID restaurantId, UUID orderId, UUID userId) {
        Restaurant restaurant = owner.requireOwnedRestaurant(restaurantId, userId);
        RestaurantOrder order = findOrThrow(orderId, restaurantId);
        if (order.markAsPrepared()) {
            events.publishEvent(new OrderReadyEvent(UUID.randomUUID(), orderId, restaurantId,
                    restaurant.getAddress(), restaurant.getLatitude(), restaurant.getLongitude(),
                    Instant.now()));
        }
        return mapper.toResponse(order);
    }

    private RestaurantOrder findOrThrow(UUID orderId, UUID restaurantId) {
        return orders.findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> new RestaurantOrderNotFoundException(orderId));
    }

    private void validate(OrderCreatedEvent e) {
        if (e.orderId() == null || e.restaurantId() == null || e.customerId() == null
                || e.total() == null || e.items() == null || e.items().isEmpty()) {
            throw new IllegalArgumentException("Malformed order.created event");
        }
    }
}
