package com.msmtech.restaurantapp.controller;

import com.msmtech.restaurantapp.dto.AuthRequest;
import com.msmtech.restaurantapp.dto.AuthResponse;
import com.msmtech.restaurantapp.dto.RegistrationRequest;
import com.msmtech.restaurantapp.entity.User;
import com.msmtech.restaurantapp.repository.UserRepository;
import com.msmtech.restaurantapp.service.CustomUserDetailsService;
import com.msmtech.restaurantapp.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        try {
            System.out.println("📝 Registration request: " + registrationRequest.getEmail());

            // Check if user already exists
            if (userRepository.existsByEmail(registrationRequest.getEmail())) {
                return ResponseEntity.badRequest().body("Error: Email is already taken!");
            }

            if (userRepository.existsByPhone(registrationRequest.getPhone())) {
                return ResponseEntity.badRequest().body("Error: Phone number is already taken!");
            }

            // Create new user account with encoded password
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setFullName(registrationRequest.getFullName());
            user.setPhone(registrationRequest.getPhone());
            user.setRole(registrationRequest.getRole());

            User savedUser = userRepository.save(user);
            System.out.println("✅ User registered: " + savedUser.getId());

            // Generate JWT token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);

            AuthResponse authResponse = new AuthResponse(
                    jwt,
                    savedUser.getEmail(),
                    savedUser.getPhone(),
                    savedUser.getFullName(),
                    savedUser.getRole()
            );

            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Registration failed - " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authRequest) {
        try {
            System.out.println("🔐 Login attempt: " + authRequest.getLoginIdentifier());

            // Validate that at least one identifier is provided
            if (!authRequest.hasValidIdentifier()) {
                return ResponseEntity.badRequest().body("Error: Email or phone is required!");
            }

            String loginIdentifier = authRequest.getLoginIdentifier();
            String password = authRequest.getPassword();

            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginIdentifier, password)
            );

            // Load user details and generate token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginIdentifier);
            final String jwt = jwtUtil.generateToken(userDetails);

            // Get user entity for response
            User user = userDetailsService.getUserEntityByIdentifier(loginIdentifier);

            AuthResponse authResponse = new AuthResponse(
                    jwt,
                    user.getEmail(),
                    user.getPhone(),
                    user.getFullName(),
                    user.getRole()
            );

            System.out.println("✅ Login successful: " + user.getEmail());
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            System.out.println("❌ Invalid credentials for: " + authRequest.getLoginIdentifier());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: Invalid credentials!");
        } catch (Exception e) {
            System.out.println("❌ Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Authentication failed - " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String testAuth() {
        return "Authentication endpoint is working!";
    }
}