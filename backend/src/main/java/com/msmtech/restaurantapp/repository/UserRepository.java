package com.msmtech.restaurantapp.repository;

import com.msmtech.restaurantapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (for login)
    Optional<User> findByEmail(String email);

    // Find user by phone (for login)
    Optional<User> findByPhone(String phone);

    // Combined query to find user by email OR phone
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.phone = :identifier")
    Optional<User> findByEmailOrPhone(@Param("identifier") String identifier);

    // Check if email exists (for registration validation)
    boolean existsByEmail(String email);

    // Check if phone exists (for registration validation)
    boolean existsByPhone(String phone);

    // Find users by role
    List<User> findByRole(String role);

    // Check if email or phone already exists (for registration)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email OR u.phone = :phone")
    boolean existsByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);
}