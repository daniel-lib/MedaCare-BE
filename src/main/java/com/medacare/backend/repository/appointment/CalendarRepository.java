package com.medacare.backend.repository.appointment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medacare.backend.model.User;
import com.medacare.backend.model.appointmentBooking.Calendar;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long>{
    List<Calendar> findAll();

  
    Calendar findByOwner(User calendarOwner);
}
