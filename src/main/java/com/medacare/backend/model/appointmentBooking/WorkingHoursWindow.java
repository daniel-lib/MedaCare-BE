package com.medacare.backend.model.appointmentBooking;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medacare.backend.model.Physician;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class WorkingHoursWindow implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OffsetDateTime fullDateTime;
    private LocalDate date;
    private DayOfWeek dayOfWeek;    
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(columnDefinition = "integer[]")
    private Integer[] offeredDurationsMinutes = new Integer[] {30, 60};

    @JsonIgnore
    @ManyToOne()
    private Physician physician;

    @JsonIgnore
    @ManyToOne
    private Calendar calendar;

    @JsonIgnore
    @OneToMany(mappedBy = "workingHoursWindow", orphanRemoval = true)
    private List<AvailabilitySlot> availabilitySlot = new ArrayList<>();
}
