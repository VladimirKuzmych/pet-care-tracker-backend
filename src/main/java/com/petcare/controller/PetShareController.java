package com.petcare.controller;

import com.petcare.dto.AcceptShareLinkRequest;
import com.petcare.dto.AcceptShareLinkResponse;
import com.petcare.dto.CreateShareLinkRequest;
import com.petcare.dto.ShareLinkResponse;
import com.petcare.service.PetSharingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PetShareController {

    private final PetSharingService petSharingService;

    public PetShareController(PetSharingService petSharingService) {
        this.petSharingService = petSharingService;
    }

    @GetMapping("/users/{userId}/pets/{petId}/share")
    public ResponseEntity<ShareLinkResponse> createShareLink(
            @PathVariable Long userId,
            @PathVariable Long petId) {
        try {
            CreateShareLinkRequest request = new CreateShareLinkRequest(userId, 24);
            ShareLinkResponse response = petSharingService.createShareLink(petId, userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/users/{userId}/share/accept")
    public ResponseEntity<AcceptShareLinkResponse> acceptShareLink(
            @PathVariable Long userId,
            @Valid @RequestBody AcceptShareLinkRequest request) {
        try {
            AcceptShareLinkResponse response = petSharingService.acceptShareLink(request.getToken(), userId);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
