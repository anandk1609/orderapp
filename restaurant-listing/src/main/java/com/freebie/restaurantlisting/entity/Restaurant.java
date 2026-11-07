package com.freebie.restaurantlisting.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "is_open", nullable = false)
    private boolean open;

    @Column(name = "rating_avg",  nullable = false, precision = 3, scale = 2)
    private BigDecimal ratingAvg = BigDecimal.ZERO;

    @Column(name = "rating_count", nullable = false)
    private int ratingCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private long version;

    @SuppressWarnings("unused")
    protected Restaurant() {} // required by JPA

    public Restaurant(UUID ownerId, String name, String address, Double latitude, Double longitude) {
        this.id = UUID.randomUUID();
        this.ownerId = ownerId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public boolean isOpen() {
        return open;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getRatingAvg() {
        return ratingAvg;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void addRating(int stars) {
        BigDecimal total = ratingAvg
                .multiply(BigDecimal.valueOf(ratingCount))
                .add(BigDecimal.valueOf(stars));
        ratingCount++;
        ratingAvg = total.divide(BigDecimal.valueOf(ratingCount), 2, RoundingMode.HALF_UP);
    }
}
