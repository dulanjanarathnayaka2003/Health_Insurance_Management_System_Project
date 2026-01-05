package com.sliit.healthins.dto;

public class PasswordResetConfirmDTO {
    private String token;
    private String newPassword;

    public PasswordResetConfirmDTO() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}


