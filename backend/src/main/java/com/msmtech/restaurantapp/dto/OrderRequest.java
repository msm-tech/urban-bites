package com.msmtech.restaurantapp.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class OrderRequest {

    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> items;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    private String specialInstructions;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private Double totalAmount;

    // Constructors
    public OrderRequest() {}

    public OrderRequest(List<OrderItemRequest> items, String deliveryAddress, String specialInstructions, Double totalAmount) {
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.specialInstructions = specialInstructions;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
}