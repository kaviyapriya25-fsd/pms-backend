package com.example.demo.dto;

public class LoginResponse {

    private boolean success;
    private String message;
    private String token;
    private Integer userId;
    private Integer companyId;
    private String name;
    private String email;
    private String role;

    public LoginResponse() {
    }

    public LoginResponse(boolean success,
                          String message,
                          String token,
                          Integer userId,
                          Integer companyId,
                          String name,
                          String email,
                          String role) {

        this.success = success;
        this.message = message;
        this.token = token;
        this.userId = userId;
        this.companyId = companyId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}