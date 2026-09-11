package com.example.demo.dto;

public class GoogleLoginRequest {

    private String email;
    private String name;
    private String googleId;
    private String role;

    public GoogleLoginRequest() {
    }

    public GoogleLoginRequest(String email, String name, String googleId, String role) {
        this.email = email;
        this.name = name;
        this.googleId = googleId;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
