package com.example.demo.dto;

public class ActivateAccountRequest {

    private String email;
    private String activationOtp;
    private String newPassword;

    public ActivateAccountRequest() {
    }

    public ActivateAccountRequest(String email, String activationOtp, String newPassword) {
        this.email = email;
        this.activationOtp = activationOtp;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivationOtp() {
        return activationOtp;
    }

    public void setActivationOtp(String activationOtp) {
        this.activationOtp = activationOtp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
