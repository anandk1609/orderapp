package com.freebie.restaurantlisting.repository;

import com.freebie.restaurantlisting.entity.RestaurantOrder;
import com.freebie.restaurantlisting.event.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, UUID> {
    Page<RestaurantOrder> findByRestaurantId(UUID restaurantId, Pageable pageable);
    Page<RestaurantOrder> findByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status, Pageable pageable);
    Optional<RestaurantOrder> findByIdAndRestaurantId(UUID id, UUID restaurantId);
}
