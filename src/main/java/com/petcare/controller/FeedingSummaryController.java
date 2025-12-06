package com.petcare.controller;

import com.petcare.dto.FeedingSummaryResponse;
import com.petcare.service.FeedingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedings/summary")
public class FeedingSummaryController {

    private final FeedingService feedingService;

    public FeedingSummaryController(FeedingService feedingService) {
        this.feedingService = feedingService;
    }

    @GetMapping("/users/{userId}/today")
    public ResponseEntity<List<FeedingSummaryResponse>> getTodaySummaryForAllUserPets(@PathVariable Long userId) {
        List<FeedingSummaryResponse> summaries = feedingService.getTodaySummaryForAllUserPets(userId);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/pets/{petId}/today")
    public ResponseEntity<FeedingSummaryResponse> getTodaySummaryForPet(@PathVariable Long petId) {
        try {
            FeedingSummaryResponse summary = feedingService.getTodaySummary(petId);
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
