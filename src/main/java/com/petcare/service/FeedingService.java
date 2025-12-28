package com.petcare.service;

import com.petcare.dto.FeedingRequest;
import com.petcare.dto.FeedingResponse;
import com.petcare.model.Feeding;
import com.petcare.model.Pet;
import com.petcare.repository.FeedingRepository;
import com.petcare.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedingService {

    private final FeedingRepository feedingRepository;
    private final PetRepository petRepository;

    public FeedingService(FeedingRepository feedingRepository, PetRepository petRepository) {
        this.feedingRepository = feedingRepository;
        this.petRepository = petRepository;
    }

    @Transactional
    public FeedingResponse recordFeeding(Long petId, FeedingRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));

        Feeding feeding = new Feeding(pet, request.getGrams(), request.getFedAt(), request.getNotes());
        Feeding savedFeeding = feedingRepository.save(feeding);

        return toResponse(savedFeeding);
    }

    public List<FeedingResponse> getFeedingHistory(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new RuntimeException("Pet not found with id: " + petId);
        }

        List<Feeding> feedings = feedingRepository.findByPetIdOrderByFedAtDesc(petId);
        return feedings.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<FeedingResponse> getFeedingHistoryByDateRange(Long petId, LocalDateTime startDate, LocalDateTime endDate) {
        if (!petRepository.existsById(petId)) {
            throw new RuntimeException("Pet not found with id: " + petId);
        }

        List<Feeding> feedings = feedingRepository.findByPetIdAndFedAtBetweenOrderByFedAtDesc(petId, startDate, endDate);
        return feedings.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<FeedingResponse> getTodayFeedings(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new RuntimeException("Pet not found with id: " + petId);
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<Feeding> feedings = feedingRepository.findByPetIdAndFedAtBetweenOrderByFedAtDesc(petId, startOfDay, endOfDay);
        return feedings.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<FeedingResponse> getFeedingById(Long id) {
        return feedingRepository.findById(id)
                .map(this::toResponse);
    }

    @Transactional
    public Optional<FeedingResponse> updateFeeding(Long id, FeedingRequest request) {
        return feedingRepository.findById(id)
                .map(feeding -> {
                    feeding.setGrams(request.getGrams());
                    feeding.setFedAt(request.getFedAt());
                    feeding.setNotes(request.getNotes());
                    Feeding updated = feedingRepository.save(feeding);
                    return toResponse(updated);
                });
    }

    @Transactional
    public boolean deleteFeeding(Long id) {
        if (feedingRepository.existsById(id)) {
            feedingRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private FeedingResponse toResponse(Feeding feeding) {
        return new FeedingResponse(
                feeding.getId(),
                feeding.getPet().getId(),
                feeding.getPet().getName(),
                feeding.getGrams(),
                feeding.getFedAt(),
                feeding.getNotes(),
                feeding.getCreatedAt(),
                feeding.getUpdatedAt()
        );
    }
}
