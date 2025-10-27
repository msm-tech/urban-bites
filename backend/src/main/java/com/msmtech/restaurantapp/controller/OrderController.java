package com.msmtech.restaurantapp.controller;

import com.msmtech.restaurantapp.dto.OrderResponse;
import com.msmtech.restaurantapp.entity.Order;
import com.msmtech.restaurantapp.entity.User;
import com.msmtech.restaurantapp.repository.OrderRepository;
import com.msmtech.restaurantapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // POST /api/orders - Create new order
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("=== DEBUG ORDER CREATION ===");
            System.out.println("Raw order createdAt: " + order.getCreatedAt());
            System.out.println("Is createdAt null? " + (order.getCreatedAt() == null));

            // Rest of your code...
            if (userDetails != null) {
                String userEmail = userDetails.getUsername();
                Optional<User> userOptional = userRepository.findByEmail(userEmail);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    order.setUser(user);
                    System.out.println("✅ Associated order with user: " + user.getEmail() + " (ID: " + user.getId() + ")");
                } else {
                    System.out.println("⚠️ User not found for email: " + userEmail);
                }
            }

            // Also set customer email from authenticated user
            if (userDetails != null && order.getCustomerEmail() == null) {
                order.setCustomerEmail(userDetails.getUsername());
            }

            Order savedOrder = orderRepository.save(order);
            System.out.println("✅ Order created successfully: " + savedOrder.getId());
            System.out.println("✅ Final createdAt: " + savedOrder.getCreatedAt());

            OrderResponse response = new OrderResponse(savedOrder);
            System.out.println("=== FRONTEND RESPONSE DEBUG ===");
            System.out.println("Response createdAt: " + response.getCreatedAt());
            System.out.println("Raw entity createdAt: " + savedOrder.getCreatedAt());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.out.println("❌ Error creating order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }

    // GET /api/orders - Get all orders (for admin, or remove if not needed)
    @GetMapping
    public ResponseEntity<?> getAllOrders(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("📋 Fetching all orders for user: " +
                (userDetails != null ? userDetails.getUsername() : "unknown"));

        // For now, return all orders. You might want to restrict this to admin users
        List<OrderResponse> orders = orderRepository.findAll().stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    // GET /api/orders/my-orders - Get orders for current authenticated user
    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        String userEmail = userDetails.getUsername();
        System.out.println("🔍 Fetching orders for authenticated user: " + userEmail);

        try {
            // Get user ID first
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            Long userId = userOptional.get().getId();
            System.out.println("👤 User ID: " + userId);

            // Try multiple approaches to find orders
            System.out.println("📊 Attempt 1: Finding orders by user ID: " + userId);
            List<Order> ordersByUserId = orderRepository.findByUserId(userId);
            System.out.println("📦 Orders found by user ID: " + ordersByUserId.size());

            System.out.println("📊 Attempt 2: Finding orders by user email: " + userEmail);
            List<Order> ordersByUserEmail = orderRepository.findByUserEmail(userEmail);
            System.out.println("📦 Orders found by user email: " + ordersByUserEmail.size());

            System.out.println("📊 Attempt 3: Finding orders by customer email: " + userEmail);
            List<Order> ordersByCustomerEmail = orderRepository.findByCustomerEmail(userEmail);
            System.out.println("📦 Orders found by customer email: " + ordersByCustomerEmail.size());

            // Combine all results
            List<Order> allUserOrders = new ArrayList<>();
            allUserOrders.addAll(ordersByUserId);
            allUserOrders.addAll(ordersByUserEmail);
            allUserOrders.addAll(ordersByCustomerEmail);

            // Remove duplicates by ID
            List<Order> uniqueOrders = allUserOrders.stream()
                    .collect(Collectors.toMap(Order::getId, Function.identity(), (existing, replacement) -> existing))
                    .values()
                    .stream()
                    .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                    .collect(Collectors.toList());

            System.out.println("✅ Final unique orders for user: " + uniqueOrders.size());

            List<OrderResponse> orderResponses = uniqueOrders.stream()
                    .map(order -> {
                        // Debug each order's date
                        System.out.println("Order #" + order.getId() + " createdAt: " + order.getCreatedAt());
                        OrderResponse response = new OrderResponse(order);
                        System.out.println("Order #" + order.getId() + " response createdAt: " + response.getCreatedAt());
                        return response;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(orderResponses);

        } catch (Exception e) {
            System.out.println("❌ Error fetching user orders: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching orders: " + e.getMessage());
        }
    }

    // GET /api/orders/user/email/{email} - Get orders by user email
    @GetMapping("/user/email/{email}")
    public ResponseEntity<?> getOrdersByUserEmail(@PathVariable String email,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Optional: Check if the requested email matches the authenticated user's email
        // This prevents users from accessing other users' orders
        if (!userDetails.getUsername().equals(email)) {
            System.out.println("🚫 Access denied: User " + userDetails.getUsername() +
                    " tried to access orders for " + email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        System.out.println("📧 Fetching orders for user email: " + email);
        try {
            List<OrderResponse> orders = orderRepository.findByUserEmail(email).stream()
                    .map(OrderResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.out.println("❌ Error fetching orders by email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching orders: " + e.getMessage());
        }
    }

    // GET /api/orders/user/phone/{phone} - Get orders by user phone
    @GetMapping("/user/phone/{phone}")
    public ResponseEntity<?> getOrdersByUserPhone(@PathVariable String phone,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        System.out.println("📞 Fetching orders for user phone: " + phone);
        try {
            List<OrderResponse> orders = orderRepository.findByUserPhone(phone).stream()
                    .map(OrderResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.out.println("❌ Error fetching orders by phone: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching orders: " + e.getMessage());
        }
    }

    // GET /api/orders/user/{userId} - Get orders by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        System.out.println("👤 Fetching orders for user ID: " + userId);
        try {
            List<OrderResponse> orders = orderRepository.findByUserId(userId).stream()
                    .map(OrderResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.out.println("❌ Error fetching orders by user ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching orders: " + e.getMessage());
        }
    }

    // GET /api/orders/{id} - Get specific order
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        System.out.println("🔍 Fetching order: " + id + " for user: " + userDetails.getUsername());
        Optional<Order> order = orderRepository.findById(id);
        return order.map(o -> ResponseEntity.ok(new OrderResponse(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/orders/{id}/status - Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id,
                                               @RequestBody String newStatus,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        System.out.println("🔄 Updating order " + id + " status to: " + newStatus +
                " by user: " + userDetails.getUsername());

        Optional<Order> orderOptional = orderRepository.findById(id);

        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOptional.get();
        try {
            order.updateStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);
            return ResponseEntity.ok(new OrderResponse(updatedOrder));
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Status update failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid status: " + e.getMessage());
        }
    }

    @GetMapping("/test/user-info")
    public ResponseEntity<?> testUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticatedUser", userDetails.getUsername());
        response.put("authorities", userDetails.getAuthorities());

        // Check if user exists in database

        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
        response.put("userExistsInDB", user.isPresent());
        if (user.isPresent()) {
            response.put("userId", user.get().getId());
        }

        // Check orders count
        long ordersCount = orderRepository.count();
        response.put("totalOrdersInDB", ordersCount);

        return ResponseEntity.ok(response);
    }

}