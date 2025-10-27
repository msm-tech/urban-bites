package com.msmtech.restaurantapp.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {

    private String email;
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    public AuthRequest() {}

    public AuthRequest(String email, String phone, String password) {
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean hasValidIdentifier() {
        return (email != null && !email.trim().isEmpty()) ||
                (phone != null && !phone.trim().isEmpty());
    }

    public String getLoginIdentifier() {
        if (email != null && !email.trim().isEmpty()) {
            return email.trim();
        } else if (phone != null && !phone.trim().isEmpty()) {
            return phone.trim();
        }
        return null;
    }

    public IdentifierType getIdentifierType() {
        if (email != null && !email.trim().isEmpty()) {
            return IdentifierType.EMAIL;
        } else if (phone != null && !phone.trim().isEmpty()) {
            return IdentifierType.PHONE;
        }
        return IdentifierType.NONE;
    }

    public enum IdentifierType {
        EMAIL, PHONE, NONE
    }
}