package com.msmtech.restaurantapp.controller;

import com.msmtech.restaurantapp.dto.OrderResponse;
import com.msmtech.restaurantapp.entity.Order;
import com.msmtech.restaurantapp.entity.User;
import com.msmtech.restaurantapp.repository.OrderRepository;
import com.msmtech.restaurantapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // POST /api/orders - Create new order
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            logger.debug("DEBUG ORDER CREATION");
            logger.debug("Raw order createdAt: {}", order.getCreatedAt());
            logger.debug("Is createdAt null? {}", (order.getCreatedAt() == null));

            // Rest of your code...
            if (userDetails != null) {
                String userEmail = userDetails.getUsername();
                Optional<User> userOptional = userRepository.findByEmail(userEmail);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    order.setUser(user);
                    logger.info("Associated order with user: {} (ID: {})", user.getEmail(), user.getId());
                } else {
                    logger.warn("User not found for email: {}", userEmail);
                }
            }

            // Also set customer email from authenticated user
            if (userDetails != null && order.getCustomerEmail() == null) {
                order.setCustomerEmail(userDetails.getUsername());
            }

            Order savedOrder = orderRepository.save(order);
            logger.info("Order created successfully: {}", savedOrder.getId());
            logger.debug("Final createdAt: {}", savedOrder.getCreatedAt());

            OrderResponse response = new OrderResponse(savedOrder);
            logger.debug("FRONTEND RESPONSE DEBUG - createdAt: {} / raw: {}", response.getCreatedAt(), savedOrder.getCreatedAt());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }

    // GET /api/orders - Get all orders (for admin, or remove if not needed)
    @GetMapping
    public ResponseEntity<?> getAllOrders(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Fetching all orders for user: {}",
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
        logger.info("Fetching orders for authenticated user: {}", userEmail);

        try {
            // Get user ID first
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            Long userId = userOptional.get().getId();
            logger.debug("User ID: {}", userId);

            // Try multiple approaches to find orders
            logger.debug("Attempt 1: Finding orders by user ID: {}", userId);
            List<Order> ordersByUserId = orderRepository.findByUserId(userId);
            logger.debug("Orders found by user ID: {}", ordersByUserId.size());

            logger.debug("Attempt 2: Finding orders by user email: {}", userEmail);
            List<Order> ordersByUserEmail = orderRepository.findByUserEmail(userEmail);
            logger.debug("Orders found by user email: {}", ordersByUserEmail.size());

            logger.debug("Attempt 3: Finding orders by customer email: {}", userEmail);
            List<Order> ordersByCustomerEmail = orderRepository.findByCustomerEmail(userEmail);
            logger.debug("Orders found by customer email: {}", ordersByCustomerEmail.size());

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

            logger.info("Final unique orders for user: {}", uniqueOrders.size());

            List<OrderResponse> orderResponses = uniqueOrders.stream()
                    .map(order -> {
                        // Debug each order's date
                        logger.debug("Order #{} createdAt: {}", order.getId(), order.getCreatedAt());
                        OrderResponse response = new OrderResponse(order);
                        logger.debug("Order #{} response createdAt: {}", order.getId(), response.getCreatedAt());
                        return response;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(orderResponses);

        } catch (Exception e) {
            logger.error("Error fetching user orders: {}", e.getMessage(), e);
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
            logger.warn("Access denied: User {} tried to access orders for {}", userDetails.getUsername(), email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        logger.info("Fetching orders for user email: {}", email);
        try {
            List<OrderResponse> orders = orderRepository.findByUserEmail(email).stream()
                    .map(OrderResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching orders by email: {}", e.getMessage(), e);
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

        logger.info("Fetching orders for user phone: {}", phone);
        try {
            List<OrderResponse> orders = orderRepository.findByUserPhone(phone).stream()
                    .map(OrderResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching orders by phone: {}", e.getMessage(), e);
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

        logger.info("Fetching orders for user ID: {}", userId);
        try {
            List<OrderResponse> orders = orderRepository.findByUserId(userId).stream()
                    .map(OrderResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching orders by user ID: {}", e.getMessage(), e);
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

        logger.info("Fetching order: {} for user: {}", id, userDetails.getUsername());
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

        logger.info("Updating order {} status to: {} by user: {}", id, newStatus, userDetails.getUsername());

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
            logger.warn("Status update failed: {}", e.getMessage());
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