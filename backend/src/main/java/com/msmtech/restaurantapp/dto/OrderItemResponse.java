package com.msmtech.restaurantapp.dto;

import com.msmtech.restaurantapp.entity.OrderItem;

public class OrderItemResponse {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private Double price;
    private Double itemTotal;

    public OrderItemResponse(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.menuItemId = orderItem.getMenuItemId();
        this.menuItemName = orderItem.getMenuItemName();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
        this.itemTotal = orderItem.getItemTotal();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }

    public String getMenuItemName() { return menuItemName; }
    public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getItemTotal() { return itemTotal; }
    public void setItemTotal(Double itemTotal) { this.itemTotal = itemTotal; }
}