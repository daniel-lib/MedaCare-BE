package com.medacare.backend.repository.appointment;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.Patient;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.appointmentBooking.Appointment;
import com.medacare.backend.model.appointmentBooking.AvailabilitySlot;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAll();

    List<Appointment> findByPhysician(Physician physician);
    List<Appointment> findByPatient(Patient patient);

    Set<Appointment> findByPhysicianIn(List<Physician> physician);

    int deleteByAvailabilitySlotIn(List<AvailabilitySlot> availabilitySlots);

    List<Appointment> findByPhysicianAndPatient(Physician physician, Patient patient);
}
