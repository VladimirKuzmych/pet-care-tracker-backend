package com.petcare.dto;

import jakarta.validation.constraints.NotBlank;

public class AcceptShareLinkRequest {

    @NotBlank(message = "Token is required")
    private String token;

    public AcceptShareLinkRequest() {
    }

    public AcceptShareLinkRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
