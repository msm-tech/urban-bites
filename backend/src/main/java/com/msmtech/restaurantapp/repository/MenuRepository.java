package com.msmtech.restaurantapp.repository;

import com.msmtech.restaurantapp.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<MenuItem, Long> {

    // Learning: Spring Data JPA Query Methods
    List<MenuItem> findByCategory(String category);

    List<MenuItem> findByPriceLessThan(Double maxPrice);

    List<MenuItem> findByNameContainingIgnoreCase(String name);

    // Learning: Custom JPQL query
    @Query("SELECT m FROM MenuItem m WHERE m.price BETWEEN :minPrice AND :maxPrice")
    List<MenuItem> findMenuItemsInPriceRange(Double minPrice, Double maxPrice);

    // Learning: Projection query
    @Query("SELECT m.name, m.price FROM MenuItem m WHERE m.category = :category")
    List<Object[]> findMenuNamesAndPricesByCategory(String category);

    boolean existsByName(String name);
}