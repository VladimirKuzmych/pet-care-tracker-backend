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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetSharingServiceTest {

    @Mock
    private PetShareTokenRepository shareTokenRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetService petService;

    @InjectMocks
    private PetSharingService petSharingService;

    private User owner;
    private User recipient;
    private Pet pet;
    private CreateShareLinkRequest request;

    @BeforeEach
    void setUp() {
        owner = new User("John", "Doe", "john@example.com", "password123");
        owner.setId(1L);

        recipient = new User("Jane", "Smith", "jane@example.com", "password456");
        recipient.setId(2L);

        pet = new Pet("Buddy", "Dog", "Golden Retriever");
        pet.setId(1L);
        pet.setUsers(new HashSet<>());
        pet.addUser(owner);

        request = new CreateShareLinkRequest(1L, 24);
    }

    @Test
    void createShareLink_Success() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(petService.isPetOwnedByUser(1L, 1L)).thenReturn(true);
        when(shareTokenRepository.save(any(PetShareToken.class))).thenAnswer(i -> {
            PetShareToken token = i.getArgument(0);
            token.setId(1L);
            return token;
        });

        ShareLinkResponse response = petSharingService.createShareLink(1L, 1L, request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getExpiresAt());
        
        verify(shareTokenRepository, times(1)).save(any(PetShareToken.class));
    }

    @Test
    void createShareLink_UserDoesNotOwnPet_ThrowsException() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petService.isPetOwnedByUser(1L, 2L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> 
            petSharingService.createShareLink(1L, 2L, request)
        );
    }

    @Test
    void acceptShareLink_Success() {
        PetShareToken shareToken = new PetShareToken(
            "test-token-123",
            pet,
            owner,
            LocalDateTime.now().plusHours(24)
        );
        shareToken.setId(1L);

        when(shareTokenRepository.findByToken("test-token-123")).thenReturn(Optional.of(shareToken));
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        AcceptShareLinkResponse response = petSharingService.acceptShareLink("test-token-123", 2L);

        assertTrue(response.isSuccess());
        assertEquals("Successfully added pet to your account", response.getMessage());
        assertNotNull(response.getPet());
        
        verify(petRepository, times(1)).save(pet);
        verify(shareTokenRepository, times(1)).delete(shareToken);
    }

    @Test
    void acceptShareLink_ExpiredToken_ReturnsFalse() {
        PetShareToken shareToken = new PetShareToken(
            "expired-token",
            pet,
            owner,
            LocalDateTime.now().minusHours(1)
        );
        shareToken.setId(1L);

        when(shareTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(shareToken));

        AcceptShareLinkResponse response = petSharingService.acceptShareLink("expired-token", 2L);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("expired"));
    }

    @Test
    void acceptShareLink_AlreadyHasAccess_ReturnsFalse() {
        pet.addUser(recipient);
        
        PetShareToken shareToken = new PetShareToken(
            "test-token",
            pet,
            owner,
            LocalDateTime.now().plusHours(24)
        );
        shareToken.setId(1L);

        when(shareTokenRepository.findByToken("test-token")).thenReturn(Optional.of(shareToken));
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));

        AcceptShareLinkResponse response = petSharingService.acceptShareLink("test-token", 2L);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("already have access"));
    }
}
