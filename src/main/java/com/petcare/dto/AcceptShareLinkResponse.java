package com.petcare.dto;

import com.petcare.model.Pet;

public class AcceptShareLinkResponse {

    private boolean success;
    private String message;
    private Pet pet;

    public AcceptShareLinkResponse() {
    }

    public AcceptShareLinkResponse(boolean success, String message, Pet pet) {
        this.success = success;
        this.message = message;
        this.pet = pet;
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

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
