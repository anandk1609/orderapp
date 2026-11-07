package com.freebie.restaurantlisting.repository;

import com.freebie.restaurantlisting.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByRestaurantIdOrderByNameAsc(UUID restaurantId);
    Optional<MenuItem> findByIdAndRestaurantId(UUID id, UUID restaurantId);
    boolean existsByRestaurantIdAndNameIgnoreCase(UUID restaurantId, String name);
}