package com.petcare.controller;

import com.petcare.dto.FeedingRequest;
import com.petcare.dto.FeedingResponse;
import com.petcare.service.FeedingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pets/{petId}/feedings")
public class FeedingController {

    private final FeedingService feedingService;

    public FeedingController(FeedingService feedingService) {
        this.feedingService = feedingService;
    }

    @PostMapping
    public ResponseEntity<FeedingResponse> recordFeeding(
            @PathVariable Long petId,
            @Valid @RequestBody FeedingRequest request) {
        try {
            FeedingResponse response = feedingService.recordFeeding(petId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<FeedingResponse>> getFeedingHistory(
            @PathVariable Long petId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<FeedingResponse> feedings;
            if (startDate != null && endDate != null) {
                feedings = feedingService.getFeedingHistoryByDateRange(petId, startDate, endDate);
            } else {
                feedings = feedingService.getFeedingHistory(petId);
            }
            return ResponseEntity.ok(feedings);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/today")
    public ResponseEntity<List<FeedingResponse>> getTodayFeedings(@PathVariable Long petId) {
        try {
            List<FeedingResponse> feedings = feedingService.getTodayFeedings(petId);
            return ResponseEntity.ok(feedings);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{feedingId}")
    public ResponseEntity<FeedingResponse> getFeeding(@PathVariable Long petId, @PathVariable Long feedingId) {
        return feedingService.getFeedingById(feedingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{feedingId}")
    public ResponseEntity<FeedingResponse> updateFeeding(
            @PathVariable Long petId,
            @PathVariable Long feedingId,
            @Valid @RequestBody FeedingRequest request) {
        return feedingService.updateFeeding(feedingId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{feedingId}")
    public ResponseEntity<Void> deleteFeeding(@PathVariable Long petId, @PathVariable Long feedingId) {
        if (feedingService.deleteFeeding(feedingId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
