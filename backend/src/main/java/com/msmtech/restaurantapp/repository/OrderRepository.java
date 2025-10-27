package com.msmtech.restaurantapp.repository;

import com.msmtech.restaurantapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find orders by user email (through user association)
    @Query("SELECT o FROM Order o WHERE o.user.email = :email ORDER BY o.createdAt DESC")
    List<Order> findByUserEmail(@Param("email") String email);

    // Find orders by customer email (direct field)
    @Query("SELECT o FROM Order o WHERE o.customerEmail = :email ORDER BY o.createdAt DESC")
    List<Order> findByCustomerEmail(@Param("email") String email);

    // Add this method - find orders by user ID
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserId(@Param("userId") Long userId);

    // Find orders by user phone
    @Query("SELECT o FROM Order o WHERE o.user.phone = :phone")
    List<Order> findByUserPhone(@Param("phone") String phone);


    // This method should exist for findAll() to work with sorting
    List<Order> findAllByOrderByCreatedAtDesc();


}