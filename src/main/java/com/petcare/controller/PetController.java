package com.petcare.controller;

import com.petcare.model.Pet;
import com.petcare.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<Pet>> getUserPets(@PathVariable Long userId) {
        return ResponseEntity.ok(petService.getPetsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Pet> createUserPet(@PathVariable Long userId, @Valid @RequestBody Pet pet) {
        try {
            Pet createdPet = petService.createPet(pet, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPet);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{petId}")
    public ResponseEntity<Pet> getUserPet(@PathVariable Long userId, @PathVariable Long petId) {
        if (!petService.isPetOwnedByUser(petId, userId)) {
            return ResponseEntity.notFound().build();
        }
        return petService.getPetById(petId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{petId}")
    public ResponseEntity<Pet> updateUserPet(@PathVariable Long userId, @PathVariable Long petId, @Valid @RequestBody Pet petDetails) {
        if (!petService.isPetOwnedByUser(petId, userId)) {
            return ResponseEntity.notFound().build();
        }
        return petService.updatePet(petId, petDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{petId}/users")
    public ResponseEntity<Void> addUserToPet(@PathVariable Long userId, @PathVariable Long petId) {
        return petService.addUserToPet(petId, userId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> removeUserFromPet(@PathVariable Long userId, @PathVariable Long petId) {
        return petService.removeUserFromPet(petId, userId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
