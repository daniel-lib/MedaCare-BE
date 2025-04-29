package com.medacare.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Interaction;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.User;
import com.medacare.backend.model.Interaction.InteractionEntityType;
import com.medacare.backend.repository.InstitutionRepository;
import com.medacare.backend.repository.InteractionRepository;
import com.medacare.backend.repository.PhysicianRepository;

import jakarta.validation.Valid;

@Service
public class InteractionService {

    AuthenticationService authenticationService;
    ResponseService responseService;
    InteractionRepository interactionRepository;
    PhysicianRepository physicianRepository;
    InstitutionRepository institutionRepository;

    public InteractionService(AuthenticationService authenticationService, ResponseService responseService,
            InteractionRepository interactionRepository, PhysicianRepository physicianRepository,
            InstitutionRepository institutionRepository) {
        this.authenticationService = authenticationService;
        this.responseService = responseService;
        this.interactionRepository = interactionRepository;
        this.physicianRepository = physicianRepository;
        this.institutionRepository = institutionRepository;
    }

    public ResponseEntity<StandardResponse> rate(Interaction interaction) {
        User interactionActor = authenticationService.getCurrentUser();
        if (interaction.getPhysicianId() == null && interaction.getInstitutionId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.createStandardResponse("error",
                    null, "Physician or Institution must be provided", null));
        }
        Physician physician = null;
        Institution institution = null;
        Integer rating = interaction.getRating();
        boolean isModification = false;
        if (interaction.getPhysicianId() != null) {
            physician = physicianRepository.findById(interaction.getPhysicianId())
                    .orElseThrow(() -> new RuntimeException("Physician not found"));
            interaction.setPhysician(physician);
            interaction.setEntityType(InteractionEntityType.PHYSICIAN);
            if (interactionRepository.existsByInteractionActorAndPhysician(interactionActor, physician)) {
                interaction = interactionRepository.findByInteractionActorAndPhysician(interactionActor, physician);
                physician.setRating(physician.getRating()-interaction.getRating());
            }
            physician.setRating(physician.getRating()+(rating));
            physicianRepository.save(physician);

        } else if (interaction.getInstitutionId() != null) {
            institution = institutionRepository.findById(interaction.getInstitutionId())
                    .orElseThrow(() -> new RuntimeException("Institution not found"));
            interaction.setInstitution(institution);
            interaction.setEntityType(InteractionEntityType.INSTITUTION);
            if (interactionRepository.existsByInteractionActorAndInstitution(interactionActor, institution)) {
                interaction = interactionRepository.findByInteractionActorAndInstitution(interactionActor, institution);
                institution.setRating(institution.getRating()-interaction.getRating());
            }
            institution.setRating(institution.getRating()+(rating));
            institutionRepository.save(institution);
        }

        interaction.setRating(rating);
        interaction.setInteractionActor(interactionActor);
        interaction.setInteractionType(Interaction.InteractionType.RATING);
        interactionRepository.save(interaction);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", null, "Rating submitted successfully", null));
    }
}
