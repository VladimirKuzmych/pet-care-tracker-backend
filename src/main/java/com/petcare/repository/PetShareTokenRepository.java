package com.petcare.repository;

import com.petcare.model.PetShareToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PetShareTokenRepository extends JpaRepository<PetShareToken, Long> {
    
    Optional<PetShareToken> findByToken(String token);
    
    List<PetShareToken> findByPetIdAndIsActiveTrue(Long petId);
    
    void deleteByExpiresAtBeforeAndIsActiveFalse(LocalDateTime dateTime);
    
    List<PetShareToken> findByCreatedByIdAndIsActiveTrue(Long userId);
}
