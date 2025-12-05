package com.petcare.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FeedingRequest {

    @NotNull(message = "Grams is required")
    @Positive(message = "Grams must be positive")
    private BigDecimal grams;

    @NotNull(message = "Feeding time is required")
    private LocalDateTime fedAt;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    public FeedingRequest() {
    }

    public FeedingRequest(BigDecimal grams, LocalDateTime fedAt, String notes) {
        this.grams = grams;
        this.fedAt = fedAt;
        this.notes = notes;
    }

    public BigDecimal getGrams() {
        return grams;
    }

    public void setGrams(BigDecimal grams) {
        this.grams = grams;
    }

    public LocalDateTime getFedAt() {
        return fedAt;
    }

    public void setFedAt(LocalDateTime fedAt) {
        this.fedAt = fedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
