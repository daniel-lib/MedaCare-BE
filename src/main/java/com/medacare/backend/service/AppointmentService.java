package com.medacare.backend.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.StandardResponse;
import com.medacare.backend.model.Patient;
import com.medacare.backend.model.Physician;
import com.medacare.backend.model.SystemSetting;
import com.medacare.backend.model.User;
import com.medacare.backend.model.appointmentBooking.Appointment;
import com.medacare.backend.model.appointmentBooking.Appointment.AppointmentStatus;
import com.medacare.backend.model.appointmentBooking.AvailabilitySlot;
import com.medacare.backend.model.appointmentBooking.Calendar;
import com.medacare.backend.model.appointmentBooking.WorkingHoursWindow;
import com.medacare.backend.repository.PatientRepository;
import com.medacare.backend.repository.PhysicianRepository;
import com.medacare.backend.repository.UserRepository;
import com.medacare.backend.repository.appointment.AppointmentRepository;
import com.medacare.backend.repository.appointment.AvailabilitySlotRepository;
import com.medacare.backend.repository.appointment.CalendarRepository;
import com.medacare.backend.repository.appointment.WorkingHoursWindowRepository;
import com.yaphet.chapa.Chapa;
import com.yaphet.chapa.exception.ChapaException;
import com.yaphet.chapa.model.VerifyResponseData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final CalendarRepository calendarRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final AuthenticationService authenticationService;
    private final PhysicianRepository physicianRepository;
    private final UserRepository userRepository;
    private final WorkingHoursWindowRepository workingHoursWindowRepository;
    private final ResponseService responseService;
    private final ChapaService chapaService;
    private final PatientRepository patientRepository;
    private final EmailService emailService;
    private final AppointmentRepository appointmentRepository;
    private final ChapaService paymentService;
    private final SystemSettingService systemSettingService;

    @Value("${VIDEO_CONFERENCING_PLATFORM_URL}")
    String meetingPlatformBaseUrl;

    public Calendar getUserCalendar(User user) {
        Calendar userCalendar = calendarRepository.findByOwner(user);
        if (userCalendar == null) {
            userCalendar = createCalendar(user);
        }
        return userCalendar;
    }

    public Calendar createCalendar(User user) {
        Calendar calendar = new Calendar();
        calendar.setOwner(user);
        calendar.setTimezone("UTC+3");
        return calendarRepository.save(calendar);
    }

    public ResponseEntity<StandardResponse> createWorkHourWindows(WorkingHoursWindow workWindow) {

        User currentUser = authenticationService.getCurrentUser();

        Optional<Physician> optPhysician = physicianRepository.findByUser(currentUser);
        if (!optPhysician.isPresent())
            throw new RuntimeException("Physician not found");

        Physician physician = optPhysician.get();

        Calendar calendar = getUserCalendar(currentUser);

        int year = workWindow.getDate().getYear();
        int month = workWindow.getDate().getMonthValue();
        int dayOfMonth = workWindow.getDate().getDayOfMonth();
        String dayOfWeek = workWindow.getDate().getDayOfWeek().toString();
        workWindow.setFullDateTime(OffsetDateTime.of(year, month, dayOfMonth, 0, 0, 0, 0, ZoneOffset.of("+03:00")));
        workWindow.setDayOfWeek(DayOfWeek.valueOf(dayOfWeek));
        workWindow.setPhysician(physician);
        workWindow.setCalendar(calendar);

        if (workingHoursWindowRepository.findByDateAndPhysician(workWindow.getDate(), physician) != null) {
            throw new RuntimeException("Working hours window already exists");
        }

        WorkingHoursWindow savedWorkingWindow = workingHoursWindowRepository.save(workWindow);

        List<AvailabilitySlot> timeSlots = calculateTimeSlots(savedWorkingWindow);

        return ResponseEntity.ok().body(responseService.createStandardResponse("success",
                timeSlots, "Working hours window saved", null));
    }

    public List<AvailabilitySlot> calculateTimeSlots(WorkingHoursWindow workWindow) {
        LocalTime startTime = workWindow.getStartTime(); // eg. "08:30:00"
        LocalTime endTime = workWindow.getEndTime(); // eg. "18:00:00"
        long slotDurationMinutes = 30;

        Physician physician = physicianRepository.findByUserId(authenticationService.getCurrentUser().getId());

        LocalTime currentSlotStart = startTime;
        List<AvailabilitySlot> availableSlots = new ArrayList<>();

        while (currentSlotStart.isBefore(endTime)) {
            LocalTime slotEnd = currentSlotStart.plus(slotDurationMinutes, ChronoUnit.MINUTES);
            if (slotEnd.isAfter(endTime)) {
                slotEnd = endTime; // set the last slot to end at the specified end time
            }

            AvailabilitySlot slot = new AvailabilitySlot();
            slot.setStartTime(currentSlotStart);
            slot.setEndTime(slotEnd);
            slot.setPhysician(physician);
            slot.setWorkingHoursWindow(workWindow);
            slot.setDate(workWindow.getDate());
            availabilitySlotRepository.save(slot);

            availableSlots.add(slot);

            currentSlotStart = slotEnd;
        }
        return availableSlots;
    }

    public List<String> getAvailableDates(long physicianId) {
        // User currentUser = authenticationService.getCurrentUser();
        Physician physician = physicianRepository.findById(physicianId)
                .orElseThrow(() -> new RuntimeException("Physician not found"));

        List<String> availableDates = availabilitySlotRepository.getAvailableDates(physicianId);
        return availableDates.isEmpty() ? new ArrayList<>() : availableDates;
    }

    public List<Integer> getAvailableDuration(long id, String date) {
        String availableDurationStr = workingHoursWindowRepository.getAvailableDurations(id, LocalDate.parse(date));
        String[] availableDurationArray = availableDurationStr.replaceAll("[{}]", "").split(",");
        List<Integer> availableDuration = new ArrayList<>();
        for (String duration : availableDurationArray) {
            availableDuration.add(Integer.parseInt(duration.trim()));
        }
        return availableDuration.size() == 0 ? new ArrayList<>() : availableDuration;
    }

    public List<AvailabilitySlot> getAvailableSlots(Physician physician, int duration, String date) {
        List<AvailabilitySlot> availabilitySlot = availabilitySlotRepository
                .findByPhysicianAndDateAndIsBooked(physician, LocalDate.parse(date), false);
        return availabilitySlot.size() == 0 ? new ArrayList<>() : availabilitySlot;
    }

    public ResponseEntity<StandardResponse> bookAppointmentSlot(long slotId) throws Throwable {
        // get slot by id
        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId).orElse(null);

        if (slot == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(responseService.createStandardResponse("error", null,
                            "Slot not found", null));
        }

        if ((slot.isBooked() || slot.isOnHold()
                || (slot.getOnHoldUntil() != null && slot.getOnHoldUntil().isAfter(OffsetDateTime.now())))
                && !slot.getUserId().equals(authenticationService.getCurrentUser().getId())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null,
                            "Slot is already booked or on hold", null));

        }

        if (slot.isOnHold() && (slot.getOnHoldUntil() != null && slot.getOnHoldUntil().isBefore(OffsetDateTime.now())))

        {
            // release the hold on the slot
            slot.setOnHold(false);
            slot.setOnHoldUntil(FixedVars.DEFAULT_ZONED_DATE_TIME);
            slot.setUserId(null);
            slot = availabilitySlotRepository.save(slot);
        }

        // set holdUntilTime for slot until payment is done
        int waitingTime = 15; // minutes
        slot.setOnHoldUntil(OffsetDateTime.now().plusMinutes(waitingTime));
        slot.setOnHold(true);
        slot.setUserId(authenticationService.getCurrentUser().getId());
        slot = availabilitySlotRepository.save(slot);

        BigDecimal MEDACARE_STANDARD_SERVICE_FEE;
        Optional<SystemSetting> savedPricingSetting = systemSettingService.getSetting("StandardSerivcePrice");
        // if (slot.getAppointment().getServiceFee() != null ||
        // slot.getAppointment().getServiceFee() != new BigDecimal(0))
        // MEDACARE_STANDARD_SERVICE_FEE = slot.getAppointment().getServiceFee();
        // else
        if (savedPricingSetting.isPresent())
            MEDACARE_STANDARD_SERVICE_FEE = new BigDecimal(savedPricingSetting.get().getValue1());
        else
            MEDACARE_STANDARD_SERVICE_FEE = new BigDecimal(100);
        // throw new RuntimeException("Service pricing information not found");

        Map<String, String> paymentRequestResponse = paymentService
                .requestPayment(authenticationService.getCurrentUser().getEmail(),
                        MEDACARE_STANDARD_SERVICE_FEE);
        if (paymentRequestResponse.get("status").equals("error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.createStandardResponse("error", null,
                            "An error occured while sending payment request.", null));
        }

        slot.setPaymentReference(paymentRequestResponse.get("ref"));
        slot.setPaymentRequestUrl(paymentRequestResponse.get("url"));
        availabilitySlotRepository.save(slot);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseService.createStandardResponse("success", paymentRequestResponse,
                        "Appointment slot has been reserved. Complete payment within " + waitingTime
                                + " minutes to officially book it.",
                        null));
    }

    public VerifyResponseData verify(long slotId) throws Throwable {
        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));
        return paymentService.verify(slot.getPaymentReference());
    }

    @Transactional
    public ResponseEntity<StandardResponse> finalizeAppointmentBooking(long slotId) throws Throwable {
        Patient patient = patientRepository.findByUser(authenticationService.getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId).orElse(null);
        if (slot == null) {
            throw new RuntimeException("Slot not found");
        }
        //// ...

        User currentUser = authenticationService.getCurrentUser();
        String responseMessage = "Appointment booking finalized successfully. appointment link will be sent to your email";
        String responseStatus = "success";
        HttpStatus responseCode = HttpStatus.OK;

        if (slot.isBooked()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null,
                            "Slot is already booked", null));
        }

        if (!slot.isOnHold()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null,
                            "Slot is not on hold", null));
        }

        if (!slot.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null,
                            "Slot is not on hold for you", null));
        }

        if (slot.getOnHoldUntil() != null &&
                slot.getOnHoldUntil().isBefore(OffsetDateTime.now())) {
            // invalidate slot data
            slot.setOnHoldUntil(FixedVars.DEFAULT_ZONED_DATE_TIME);
            slot.setOnHold(false);
            slot.setUserId(null);
            slot = availabilitySlotRepository.save(slot);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.createStandardResponse("error", null,
                            "Slot reservation has expired. please book again", null));

        }

        if (slot.isOnHold() && (slot.getOnHoldUntil() != null &&
                slot.getOnHoldUntil().isAfter(OffsetDateTime.now())) && slot.getUserId().equals(currentUser.getId())) {

            VerifyResponseData paymentVerification;
            try {
                paymentVerification = verify(slotId);
            } catch (Throwable e) {
                throw e;
            }

            if (paymentVerification.getData().getStatus().equals("pending")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(responseService.createStandardResponse("error", null,
                                "Payment has not been completed.", null));
            }

            slot.setOnHoldUntil(FixedVars.DEFAULT_ZONED_DATE_TIME);
            slot.setBooked(true);
            slot = availabilitySlotRepository.save(slot);

            Appointment appointment = new Appointment();
            try {
                appointment.setAvailabilitySlot(slot);
                appointment.setPhysician(slot.getPhysician());
                if (slot.getPhysician().getOrgnanizationAffiliated() != null
                        && slot.getPhysician().getOrgnanizationAffiliated() == true &&
                        slot.getPhysician().getHealthcareProvider() != null) {
                    appointment.setInstitutionId(slot.getPhysician().getHealthcareProvider().getId());
                }
                appointment.setPatient(patient);
                appointment.setAppointmentDate(slot.getDate());
                appointment.setAppointmentStartTime(slot.getStartTime()); // format: 2007-12-03T10:15:30+01:00
                appointment.setAppointmentEndTime(slot.getEndTime());
                appointment.setMeetingLink(meetingPlatformBaseUrl + "MedaCare_" + UUID.randomUUID().toString());
                appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
                appointment = appointmentRepository.save(appointment);
            } catch (Exception e) {
                availabilitySlotRepository.delete(slot);
                e.printStackTrace();
                throw new RuntimeException("Error saving appointment");
            }

            try {

                if (emailService.sendAppointmentEmail(appointment, patient.getUser().getEmail()) == 0) {
                    responseMessage = "Email not sent. Appointment not sent to patient";
                    responseStatus = "error";
                    responseCode = HttpStatus.INTERNAL_SERVER_ERROR;
                }
                if (emailService.sendAppointmentEmail(appointment, slot.getPhysician().getUser().getEmail()) == 0) {
                    responseMessage = "Email not sent. Appointment not sent to physician";
                    responseStatus = "error";
                    responseCode = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error sending appointment email");
            }

        } else {
            responseMessage = "Appointment was not booked. Probably already booked or on hold by another user or unavailable venue.";
            responseStatus = "error";
            responseCode = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(responseCode)
                .body(responseService.createStandardResponse(responseStatus, null, responseMessage, null));
    }

    public List<Appointment> findPhysicianAppointment(Physician physician) {
        List<Appointment> appointments = appointmentRepository.findByPhysician(physician);
        appointments = updateAppointments(appointments);
        return appointments;
    }

    public List<Appointment> findPatientAppointment(Patient patient) {
        List<Appointment> appointments = appointmentRepository.findByPatient(patient);
        appointments = updateAppointments(appointments);
        return appointments;
    }

    public List<Appointment> getAppointmentsByPhysicians(List<Physician> physician) {
        Set<Appointment> appointmentsSet = appointmentRepository.findByPhysicianIn(physician);
        List<Appointment> appointments = appointmentsSet.stream()
                .collect(Collectors.toList());
        appointments = updateAppointments(appointments);
        return appointments;
    }

    public ResponseEntity<StandardResponse> getAppointmentsByPatientAndPhysician(Long entityId) {
        User currentUser = authenticationService.getCurrentUser();
        Physician physician;
        Patient patient;

        if (currentUser.getRole().getName().equals("PHYSICIAN")) {
            patient = patientRepository.findById(entityId)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            physician = physicianRepository.findByUser(authenticationService.getCurrentUser())
                    .orElseThrow(() -> new RuntimeException("Physician not found"));

        } else if (currentUser.getRole().getName().equals("PATIENT")) {
            physician = physicianRepository.findById(entityId)
                    .orElseThrow(() -> new RuntimeException("Physician not found"));
            patient = patientRepository.findByUser(authenticationService.getCurrentUser())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(responseService.createStandardResponse("error", null,
                            "You are not authorized to view this resource", null));
        }

        List<Appointment> appointments = appointmentRepository.findByPhysicianAndPatient(physician, patient);
        appointments = updateAppointments(appointments);

        return ResponseEntity.ok()
                .body(responseService.createStandardResponse("success", appointments == null ? new ArrayList<>()
                        : appointments, "Appointments retrieved", null));
    }

    public List<Appointment> updateAppointments(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            appointment = updateAppointment(appointment);
        }
        return appointments;
    }

    public Appointment updateAppointment(Appointment appointment) {
        boolean isInPast = appointment.getAppointmentDate().isBefore(LocalDate.now()) &&
                appointment.getAppointmentEndTime().isBefore(LocalTime.now());

        boolean isNow = appointment.getAppointmentDate().isEqual(LocalDate.now())
                &&
                (appointment.getAppointmentStartTime().isBefore(LocalTime.now())
                        && appointment.getAppointmentEndTime().isAfter(LocalTime.now()));

        if (appointment.getStatus() != AppointmentStatus.CANCELED) {
            if (appointment.getStatus() != AppointmentStatus.AWAITING_DOCUMENTATION ||
                    appointment.getStatus() != AppointmentStatus.COMPLETED) { // is Scheduled or in progress
                // update appointment status if needed
                if (isNow) {
                    appointment.setStatus(Appointment.AppointmentStatus.IN_PROGRESS);
                }

            } else if (appointment.getStatus() != AppointmentStatus.AWAITING_DOCUMENTATION ||
                    appointment.getStatus() == AppointmentStatus.COMPLETED) { // is completed
                if (isInPast) {
                    appointment.setStatus(Appointment.AppointmentStatus.AWAITING_DOCUMENTATION);
                }
            } else { // is scheduled
                if (isInPast) {
                    appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
                } else if (isNow) {
                    appointment.setStatus(Appointment.AppointmentStatus.IN_PROGRESS);
                }
            }
        }

        return appointment;
    }

}
