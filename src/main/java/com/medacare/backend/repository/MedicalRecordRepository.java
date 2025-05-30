package com.medacare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.MedicalRecord;
import com.medacare.backend.model.Patient;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.appointmentBooking.Appointment;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findAll();

    boolean existsByAppointment(Appointment appointment);

    List<MedicalRecord> getByPatient(Patient patient);
    List<MedicalRecord> getByPhysician(Physician physician);
}
