package com.msmtech.restaurantapp.controller;

import com.msmtech.restaurantapp.entity.MenuItem;
import com.msmtech.restaurantapp.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuRepository menuRepository;

    // Add this method to initialize sample data
    @PostConstruct
    public void initSampleData() {
        if (menuRepository.count() == 0) {
            System.out.println("📝 Initializing sample menu data...");

            List<MenuItem> sampleItems = Arrays.asList(
                    new MenuItem("Margherita Pizza", "Classic cheese and tomato", 12.99, "MAIN_COURSE"),
                    new MenuItem("Caesar Salad", "Fresh greens with Caesar dressing", 8.99, "APPETIZER"),
                    new MenuItem("Chocolate Cake", "Rich chocolate dessert", 6.99, "DESSERT"),
                    new MenuItem("Iced Tea", "Refreshing beverage", 2.99, "BEVERAGE")
            );

            menuRepository.saveAll(sampleItems);
            System.out.println("✅ Sample menu data initialized!");
        }
    }

    @GetMapping
    public List<MenuItem> getAllMenuItems() {
        return menuRepository.findAll();
    }

    @GetMapping("/category/{category}")
    public List<MenuItem> getMenuItemsByCategory(@PathVariable String category) {
        return menuRepository.findByCategory(category);
    }
}