package com.petcare.repository;

import com.petcare.model.Feeding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedingRepository extends JpaRepository<Feeding, Long> {
    
    List<Feeding> findByPetIdOrderByFedAtDesc(Long petId);
    
    List<Feeding> findByPetIdAndFedAtBetweenOrderByFedAtDesc(Long petId, LocalDateTime startDate, LocalDateTime endDate);
}
