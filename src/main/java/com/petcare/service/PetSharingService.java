package com.petcare.service;

import com.petcare.dto.AcceptShareLinkResponse;
import com.petcare.dto.CreateShareLinkRequest;
import com.petcare.dto.ShareLinkResponse;
import com.petcare.model.Pet;
import com.petcare.model.PetShareToken;
import com.petcare.model.User;
import com.petcare.repository.PetRepository;
import com.petcare.repository.PetShareTokenRepository;
import com.petcare.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PetSharingService {

    private static final int TOKEN_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final PetShareTokenRepository shareTokenRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PetService petService;

    public PetSharingService(PetShareTokenRepository shareTokenRepository,
                            PetRepository petRepository,
                            UserRepository userRepository,
                            PetService petService) {
        this.shareTokenRepository = shareTokenRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.petService = petService;
    }

    @Transactional
    public ShareLinkResponse createShareLink(Long petId, Long userId, CreateShareLinkRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        if (!petService.isPetOwnedByUser(petId, userId)) {
            throw new RuntimeException("User does not have access to this pet");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(request.getExpirationHours());

        PetShareToken shareToken = new PetShareToken(
                token,
                pet,
                user,
                expiresAt
        );

        shareToken = shareTokenRepository.save(shareToken);

        return new ShareLinkResponse(
                token,
                shareToken.getExpiresAt()
        );
    }

    @Transactional
    public AcceptShareLinkResponse acceptShareLink(String token, Long userId) {
        PetShareToken shareToken = shareTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid share link"));

        if (!shareToken.isValid()) {
            return new AcceptShareLinkResponse(false, "Share link has expired or is no longer valid", null);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pet pet = shareToken.getPet();

        if (pet.getUsers().contains(user)) {
            return new AcceptShareLinkResponse(false, "You already have access to this pet", pet);
        }

        pet.addUser(user);
        petRepository.save(pet);

        shareTokenRepository.delete(shareToken);

        return new AcceptShareLinkResponse(true, "Successfully added pet to your account", pet);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        shareTokenRepository.deleteByExpiresAtBeforeAndIsActiveFalse(LocalDateTime.now().minusDays(7));
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
