package com.example.MB_beauty_club_frontend.controllers;

import com.example.MB_beauty_club_frontend.clients.AppointmentClient;
import com.example.MB_beauty_club_frontend.clients.ServiceClient;
import com.example.MB_beauty_club_frontend.clients.VacationsClient;
import com.example.MB_beauty_club_frontend.clients.WorkerClient;
import com.example.MB_beauty_club_frontend.clients.WorkingHoursClient;
import com.example.MB_beauty_club_frontend.dtos.AppointmentDTO;
import com.example.MB_beauty_club_frontend.dtos.ServiceDTO;
import com.example.MB_beauty_club_frontend.dtos.VacationDTO;
import com.example.MB_beauty_club_frontend.dtos.WorkerDTO;
import com.example.MB_beauty_club_frontend.dtos.WorkingHoursDTO;
import com.example.MB_beauty_club_frontend.enums.WorkerCategory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/appointments")
public class AppointmentsController {

    private final AppointmentClient appointmentClient;
    private final ServiceClient serviceClient;
    private final WorkerClient workerClient;
    private final WorkingHoursClient workingHoursClient;
    private final VacationsClient vacationsClient;


    @GetMapping("/select_worker/{id}")
    public String showSelectWorkerForm(@PathVariable Long id,  Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");

        ServiceDTO serviceDTO = serviceClient.getServiceById(id, token);


        List<WorkerDTO> workers = workerClient.getWorkersByCategory(serviceDTO.getCategory(), token);
        model.addAttribute("workers", workers);
        model.addAttribute("category", serviceDTO.getCategory());
        model.addAttribute("serviceId", id);
        return "Appointments/select_worker";
    }

    @GetMapping("/calendar/{workerId}")
    public String showCalendar(@PathVariable Long workerId, @RequestParam("serviceId") Long serviceId, Model model, HttpServletRequest request, @RequestParam(name = "date", required = false) String dateStr) {
        String token = (String) request.getSession().getAttribute("sessionToken");

        LocalDate date = (dateStr == null) ? LocalDate.now() : LocalDate.parse(dateStr);
        model.addAttribute("currentDate", date);
        model.addAttribute("workerId", workerId);
        model.addAttribute("serviceId", serviceId);



        // Add today's date to the model for a clean comparison in Thymeleaf
        model.addAttribute("today", LocalDate.now());

        // Fetch worker, service, working hours, and vacations
        ServiceDTO service = serviceClient.getServiceById(serviceId, token);
        WorkerDTO worker = workerClient.findById(workerId, token);

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setWorker(worker);
        appointmentDTO.setService(service);
        model.addAttribute("appointmentDTO", appointmentDTO);

        List<AppointmentDTO> workerAppointments = appointmentClient.getWorkerAppointments(workerId, token);
        List<WorkingHoursDTO> workerWorkingHours = workingHoursClient.getWorkingHoursByWorkerId(workerId, token);
        List<VacationDTO> workerVacations = vacationsClient.getVacationsByWorkerId(workerId, token);

        // Check for vacation
        boolean isVacation = workerVacations.stream()
                .anyMatch(v -> !date.isBefore(v.getStartDate()) && !date.isAfter(v.getEndDate()));
        if (isVacation) {
            model.addAttribute("isNotAvailable", true);
            model.addAttribute("notAvailableReason", "Този служител е в отпуск на тази дата.");
            return "Appointments/calendar";
        }

        // Check for working day
        Optional<WorkingHoursDTO> workingDay = workerWorkingHours.stream()
                .filter(wh -> wh.getDayOfWeek().equals(date.getDayOfWeek()))
                .findFirst();

        if (workingDay.isEmpty()) {
            model.addAttribute("isNotAvailable", true);
            model.addAttribute("notAvailableReason", "Това е почивен ден за служителя.");
            return "Appointments/calendar";
        }

        // Filter appointments for the selected date
        List<AppointmentDTO> appointmentsForDate = workerAppointments.stream()
                .filter(a -> a.getStartTime().toLocalDate().isEqual(date))
                .collect(Collectors.toList());

        // Generate available time slots
        List<LocalTime> availableTimes = generateTimeSlots(appointmentsForDate, workingDay.get(), service.getDuration());
        model.addAttribute("availableTimes", availableTimes);



        return "Appointments/calendar";
    }

    @PostMapping("/book")
    public String bookAppointment(@ModelAttribute AppointmentDTO appointmentDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");

        // The DTO from the form has worker.id and service.id populated.
        // We fetch the full objects to avoid issues with JSON serialization.
        WorkerDTO worker = workerClient.findById(appointmentDTO.getWorker().getId(), token);
        ServiceDTO service = serviceClient.getServiceById(appointmentDTO.getService().getId(), token);


        appointmentDTO.setWorker(worker);
        appointmentDTO.setService(service);

        // Now you can safely save the appointment.
        appointmentClient.bookAppointment(appointmentDTO, token);
        return "redirect:/appointments/my-appointments";
    }

    @GetMapping("/my-appointments")
    public String showMyAppointments(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<AppointmentDTO> myAppointments = appointmentClient.getMyAppointments(token);
        model.addAttribute("appointments", myAppointments);
        return "Appointments/my_appointments";
    }

    @PostMapping("/delete")
    public String deleteAppointment(@RequestParam("id") Long id, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        appointmentClient.deleteAppointment(id, token);
        return "redirect:/appointments/my-appointments";
    }

    // Helper method to generate time slots based on working hours and service duration
    private List<LocalTime> generateTimeSlots(List<AppointmentDTO> bookedAppointments, WorkingHoursDTO workingHours, int serviceDurationMinutes) {
        List<LocalTime> allTimes = new ArrayList<>();
        LocalTime start = workingHours.getStartTime();
        LocalTime end = workingHours.getEndTime();

        while (start.plusMinutes(serviceDurationMinutes).isBefore(end) || start.plusMinutes(serviceDurationMinutes).equals(end)) {
            final LocalTime slotStart = start;
            final LocalTime slotEnd = start.plusMinutes(serviceDurationMinutes);

            boolean isConflicting = bookedAppointments.stream()
                    .anyMatch(booked ->
                            // Check for overlap
                            (slotStart.isBefore(booked.getEndTime().toLocalTime()) && slotEnd.isAfter(booked.getStartTime().toLocalTime())) ||
                                    // Check for exact match
                                    (slotStart.equals(booked.getStartTime().toLocalTime()) && slotEnd.equals(booked.getEndTime().toLocalTime()))
                    );

            if (!isConflicting) {
                allTimes.add(slotStart);
            }
            start = start.plus(30, ChronoUnit.MINUTES);
        }

        return allTimes;
    }
}
