package com.medacare.backend.repository.appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.medacare.backend.model.Physician;
import com.medacare.backend.model.appointmentBooking.WorkingHoursWindow;

public interface WorkingHoursWindowRepository extends JpaRepository<WorkingHoursWindow, Long> {
    List<WorkingHoursWindow> findAll();

    List<WorkingHoursWindow> findByPhysician(Physician physician);

    WorkingHoursWindow findByDateAndPhysicianAndStartTimeAndEndTime(LocalDate date, Physician physician,
            LocalTime startTime, LocalTime endDate);

    WorkingHoursWindow findByDateAndPhysician(LocalDate date, Physician physician);

    @Query(value = "SELECT DISTINCT offered_durations_minutes FROM working_hours_window WHERE date = :date AND physician_id = :id", nativeQuery = true)
    String getAvailableDurations(long id, LocalDate date);


    // @Query(value = "SELECT offered_durations_minutes FROM working_hours_window WHERE date = da AND physician_id = ?1", nativeQuery = true)
    // List<Object[]> getAvailableDurationsRaw(long physicianId, LocalDate date);


}
