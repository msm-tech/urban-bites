package com.msmtech.restaurantapp.dto;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String phone;
    private String fullName;
    private String role;

    public AuthResponse() {}

    public AuthResponse(String token, String email, String phone, String fullName, String role) {
        this.token = token;
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='[HIDDEN]'" +
                ", type='" + type + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}