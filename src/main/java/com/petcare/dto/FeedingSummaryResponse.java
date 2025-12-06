package com.petcare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeedingSummaryResponse {

    private Long petId;
    private String petName;
    private LocalDate date;
    private Integer totalFeedings;
    private BigDecimal totalGrams;
    private BigDecimal averageGramsPerFeeding;

    public FeedingSummaryResponse() {
    }

    public FeedingSummaryResponse(Long petId, String petName, LocalDate date, Integer totalFeedings, BigDecimal totalGrams, BigDecimal averageGramsPerFeeding) {
        this.petId = petId;
        this.petName = petName;
        this.date = date;
        this.totalFeedings = totalFeedings;
        this.totalGrams = totalGrams;
        this.averageGramsPerFeeding = averageGramsPerFeeding;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getTotalFeedings() {
        return totalFeedings;
    }

    public void setTotalFeedings(Integer totalFeedings) {
        this.totalFeedings = totalFeedings;
    }

    public BigDecimal getTotalGrams() {
        return totalGrams;
    }

    public void setTotalGrams(BigDecimal totalGrams) {
        this.totalGrams = totalGrams;
    }

    public BigDecimal getAverageGramsPerFeeding() {
        return averageGramsPerFeeding;
    }

    public void setAverageGramsPerFeeding(BigDecimal averageGramsPerFeeding) {
        this.averageGramsPerFeeding = averageGramsPerFeeding;
    }
}
