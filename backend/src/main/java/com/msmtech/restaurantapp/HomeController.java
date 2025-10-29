package com.msmtech.restaurantapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String home(Model model) {
        logger.debug("HomeController executed");
        model.addAttribute("restaurantName", "Urban Bites");
        model.addAttribute("tagline", "Delicious Food, Delivered Fresh");
        logger.debug("Attributes added to model");
        return "home";
    }
}
