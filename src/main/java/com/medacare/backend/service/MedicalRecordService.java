package com.medacare.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.MedicalRecord;
import com.medacare.backend.model.Patient;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.appointmentBooking.Appointment;
import com.medacare.backend.model.appointmentBooking.Appointment.AppointmentStatus;
import com.medacare.backend.repository.MedicalRecordRepository;
import com.medacare.backend.repository.PatientRepository;
import com.medacare.backend.repository.appointment.AppointmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

        private final ResponseService responseService;
        private final MedicalRecordRepository medicalRecordRepository;
        private final AppointmentRepository appointmentRepository;
        private final PatientRepository patientRepository;

        public ResponseEntity<StandardResponse> getMedicalRecordsByPatientId(Long patientId) {
                Patient patient = patientRepository.findById(patientId)
                                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
                List<MedicalRecord> medicalRecords = medicalRecordRepository.getByPatient(patient);
                return ResponseEntity
                                .ok(responseService.createStandardResponse("success",
                                                medicalRecords.isEmpty() ? new ArrayList() : medicalRecords,
                                                "Patient medical record retrieved", null));
        }

        public ResponseEntity<StandardResponse> addToMedicalRecord(MedicalRecord medicalRecord, Long appointmentId) {
                Appointment appointment = appointmentRepository.findById(appointmentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));


                // if (List.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELED, AppointmentStatus.SCHEDULED) appointment.getStatus() != AppointmentStatus.COMPLETED) {

                if (medicalRecordRepository.existsByAppointment(appointment)) {
                        return ResponseEntity
                                        .badRequest()
                                        .body(responseService.createStandardResponse("error", null,
                                                        "Record already exists", null));
                }
                Patient patient = appointment.getPatient();
                Physician physician = appointment.getPhysician();
                medicalRecord.setPatient(patient);
                medicalRecord.setPhysician(physician);
                medicalRecord.setAppointment(appointment);
                medicalRecord.setVisitDate(appointment.getAppointmentDate());

                medicalRecordRepository.save(medicalRecord);
                appointment.setStatus(AppointmentStatus.COMPLETED);
                appointmentRepository.save(appointment);

                return ResponseEntity
                                .ok(responseService.createStandardResponse("success", null,
                                                "Record added to patient medical record",
                                                null));

        }
}
