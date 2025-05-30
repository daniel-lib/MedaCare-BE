package com.medacare.backend.model.appointmentBooking;

import java.io.Serializable;

import com.medacare.backend.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Calendar implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private User owner;
    private String timezone = "UTC";


}
