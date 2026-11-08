package com.freebie.restaurantlisting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OrderLine {

    @Column(name = "item_name", nullable = false, length = 120)
    private String itemName;

    @Column(nullable = false)
    private int quantity;

    @SuppressWarnings("unused")
    protected OrderLine() {} // required for JPA

    public OrderLine(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
}
