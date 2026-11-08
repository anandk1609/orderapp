package com.freebie.restaurantlisting.entity;

import com.freebie.restaurantlisting.event.OrderStatus;
import com.freebie.restaurantlisting.exception.InvalidOrderStateException;
import jakarta.persistence.Version;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "restaurant_orders")
public class RestaurantOrder {

    @Id
    private UUID id;

    @Column(name = "restaurant_id", nullable = false, updatable = false)
    private UUID restaurantId;

    @Column(name = "customer_id", nullable = false, updatable = false)
    private UUID customerId;

    @Column(nullable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @ElementCollection
    @CollectionTable(name = "restaurant_order_lines", joinColumns = @JoinColumn(name = "order_id"))
    @BatchSize(size = 50)
    private List<OrderLine> lines = new ArrayList<>();

    @Version
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @SuppressWarnings("unused")
    protected RestaurantOrder() {} //required for JPA

    public RestaurantOrder(UUID id, UUID restaurantId, UUID customerId,
                           BigDecimal total, List<OrderLine> lines) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.customerId = customerId;
        this.total = total;
        this.lines = lines;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public boolean accept() {
        if (status == OrderStatus.ACCEPTED) return false;
        requireStatus(OrderStatus.PENDING, "accept");
        status = OrderStatus.ACCEPTED;
        updatedAt = Instant.now();
        return true;
    }

    public boolean reject(String reason) {
        if (status == OrderStatus.ACCEPTED) return false;
        requireStatus(OrderStatus.PENDING, "reject");
        status = OrderStatus.REJECTED;
        rejectionReason = reason;
        updatedAt = Instant.now();
        return true;
    }

    public boolean markAsPrepared() {
        if (status == OrderStatus.PREPARED) return false;
        requireStatus(OrderStatus.ACCEPTED, "mark as prepared");
        status = OrderStatus.PREPARED;
        updatedAt = Instant.now();
        return true;
    }

    private void requireStatus(OrderStatus expected, String action) {
        if (status != expected) {
            throw new InvalidOrderStateException(id, status, action);
        }
    }

    public UUID getId() { return id; }
    public UUID getRestaurantId() { return restaurantId; }
    public UUID getCustomerId() { return customerId; }
    public BigDecimal getTotal() { return total; }
    public OrderStatus getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public List<OrderLine> getLines() { return lines; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
