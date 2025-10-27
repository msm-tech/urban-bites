package com.msmtech.restaurantapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore // Prevent circular reference
    private User user;

    // Make sure you have customer email field if you're storing it separately
    @Column(name = "customer_email")
    private String customerEmail;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // Manage the serialization of this side
    private List<OrderItem> items = new ArrayList<>();

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotBlank
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @NotBlank
    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "special_instructions")
    private String specialInstructions;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
        // Let calculateTotal() handle totalAmount in @PreUpdate
    }

    // JPA requires default constructor
    public Order() {}

    public Order(User user, String customerName, String customerPhone,
                 String deliveryAddress, String specialInstructions, List<OrderItem> items) {
        this.user = user;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.deliveryAddress = deliveryAddress;
        this.specialInstructions = specialInstructions;
        this.items = items;
        this.totalAmount = calculateTotalAmount();
        /*this.status = "PENDING";
        this.createdAt = LocalDateTime.now(); // This should work, but let's check*/
    }

    // Getters and Setters (keep all existing ones)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) {
        this.items = items;
        if (items != null) {
            items.forEach(item -> item.setOrder(this));
        }
        calculateTotal();
    }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        // Only set if not null, otherwise keep the default value
        if (createdAt != null) {
            this.createdAt = createdAt;
        }
        // If null, do nothing - keep the default LocalDateTime.now()
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    // Rest of your methods (calculateTotal, addItem, etc.) remain the same
   /* @PrePersist*/
    @PreUpdate
    public void calculateTotal() {
        this.totalAmount = calculateTotalAmount();
    }

    private Double calculateTotalAmount() {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(OrderItem::getItemTotal)
                .sum();
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotal();
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        calculateTotal();
    }

    public void updateStatus(String newStatus) {
        List<String> validStatuses = List.of("PENDING", "CONFIRMED", "PREPARING", "COMPLETED", "CANCELLED");
        if (!validStatuses.contains(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
        this.status = newStatus;
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", customer=" + customerName + ", total=" + totalAmount + "}";
    }
}