package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.Institution;
import com.medacare.backend.model.Interaction;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.User;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
   List<Interaction> findAll();
//    Interaction findById(Long id);
   Interaction findByInteractionActor(User interactionActor);
   boolean existsByInteractionActorAndPhysician(User interactionActor, Physician physician);
   boolean existsByInteractionActorAndInstitution(User interactionActor, Institution institution);

   Interaction findByInteractionActorAndPhysician(User interactionActor, Physician physician);
   Interaction findByInteractionActorAndInstitution(User interactionActor, Institution institution);

}
