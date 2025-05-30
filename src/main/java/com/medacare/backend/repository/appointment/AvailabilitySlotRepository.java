package com.medacare.backend.repository.appointment;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.Physician;
import com.medacare.backend.model.appointmentBooking.AvailabilitySlot;
import com.medacare.backend.model.appointmentBooking.WorkingHoursWindow;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findAll();

    @Query(value = "SELECT DISTINCT date FROM availability_slot WHERE physician_id = ?1 AND is_booked = false", nativeQuery = true)
    List<String> getAvailableDates(long physicianId);


    // @Query(value = "SELECT DISTINCT * FROM working_hours_window WHERE date = :date AND physician_id = :id", nativeQuery = true)
    List<AvailabilitySlot> findByPhysicianAndDateAndIsBooked(Physician physician, LocalDate date, boolean booked);

    int deleteByWorkingHoursWindow(WorkingHoursWindow workingHourWindow);
}
