package com.medacare.backend.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Interaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "interactionActor", referencedColumnName = "id")
    private User interactionActor;

    @ManyToOne()
    @JoinColumn(name = "physician_id", referencedColumnName = "id")
    private Physician physician;

    @Transient
    private Long physicianId;

    @ManyToOne
    @JoinColumn(name = "institution_id", referencedColumnName = "id")
    private Institution institution;

    @Transient
    private Long institutionId;

    @Min(value = 0, message = "Minimum rating value is 0")
    @Max(value = 5, message = "Maximum rating value is 5")
    private Integer rating;

    @Enumerated(EnumType.STRING)
    private InteractionType interactionType;

    @Enumerated(EnumType.STRING)
    private InteractionEntityType entityType;

    public enum InteractionType {
        RATING,
        VIEW,
    }

    public enum InteractionEntityType {
        INSTITUTION,
        PHYSICIAN
    }
}
