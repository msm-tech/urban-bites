package com.msmtech.restaurantapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        System.out.println("=== HomeController executed ===");  // Add this line
        model.addAttribute("restaurantName", "Urban Bites");
        model.addAttribute("tagline", "Delicious Food, Delivered Fresh");
        System.out.println("=== Attributes added to model ===");  // Add this line
        return "home";
    }
}
