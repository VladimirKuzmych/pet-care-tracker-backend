package com.petcare.service;

import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.repository.PetRepository;
import com.petcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PetService(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public List<Pet> getPetsByUserId(Long userId) {
        return petRepository.findByUserId(userId);
    }

    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    @Transactional
    public Pet createPet(Pet pet, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pet savedPet = petRepository.save(pet);
        savedPet.addUser(user);
        return petRepository.save(savedPet);
    }

    @Transactional
    public Optional<Pet> updatePet(Long id, Pet petDetails) {
        return petRepository.findById(id)
                .map(pet -> {
                    pet.setName(petDetails.getName());
                    pet.setKind(petDetails.getKind());
                    pet.setBreed(petDetails.getBreed());
                    pet.setDailyPortion(petDetails.getDailyPortion());
                    return petRepository.save(pet);
                });
    }

    @Transactional
    public boolean addUserToPet(Long petId, Long userId) {
        Optional<Pet> petOpt = petRepository.findById(petId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (petOpt.isPresent() && userOpt.isPresent()) {
            Pet pet = petOpt.get();
            User user = userOpt.get();
            pet.addUser(user);
            petRepository.save(pet);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean removeUserFromPet(Long petId, Long userId) {
        Optional<Pet> petOpt = petRepository.findById(petId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (petOpt.isPresent() && userOpt.isPresent()) {
            Pet pet = petOpt.get();
            User user = userOpt.get();
            pet.removeUser(user);
            petRepository.save(pet);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deletePet(Long id) {
        return petRepository.findById(id)
                .map(pet -> {
                    petRepository.delete(pet);
                    return true;
                })
                .orElse(false);
    }

    public boolean isPetOwnedByUser(Long petId, Long userId) {
        return petRepository.findById(petId)
                .map(pet -> pet.getUsers().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .orElse(false);
    }
}
