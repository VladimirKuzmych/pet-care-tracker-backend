package com.petcare.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateShareLinkRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Expiration hours is required")
    @Min(value = 1, message = "Expiration must be at least 1 hour")
    @Max(value = 168, message = "Expiration cannot exceed 168 hours (7 days)")
    private Integer expirationHours;

    public CreateShareLinkRequest() {
    }

    public CreateShareLinkRequest(Long userId, Integer expirationHours) {
        this.userId = userId;
        this.expirationHours = expirationHours;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getExpirationHours() {
        return expirationHours;
    }

    public void setExpirationHours(Integer expirationHours) {
        this.expirationHours = expirationHours;
    }
}
