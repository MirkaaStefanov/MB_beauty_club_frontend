package com.example.MB_beauty_club_frontend.controllers;

import com.example.MB_beauty_club_frontend.clients.AppointmentClient;
import com.example.MB_beauty_club_frontend.clients.ServiceClient;
import com.example.MB_beauty_club_frontend.clients.WorkerClient;
import com.example.MB_beauty_club_frontend.dtos.AppointmentDTO;
import com.example.MB_beauty_club_frontend.dtos.ServiceDTO;
import com.example.MB_beauty_club_frontend.dtos.WorkerDTO;
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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/appointments")
public class AppointmentsController {

    private final AppointmentClient appointmentClient;
    private final ServiceClient serviceClient;
    private final WorkerClient workerClient;

    @GetMapping("/book")
    public String showBookAppointmentForm(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        List<ServiceDTO> allServices = serviceClient.getAllServices(token);

        // Group services by category for the user to select from
        model.addAttribute("services", allServices.stream().collect(Collectors.groupingBy(ServiceDTO::getCategory)));
        return "Appointments/book";
    }

    @GetMapping("/select_worker")
    public String showSelectWorkerForm(Model model, HttpServletRequest request, @RequestParam("category") WorkerCategory category) {
        String token = (String) request.getSession().getAttribute("sessionToken");

        List<WorkerDTO> workers = workerClient.getWorkersByCategory(category, token);
        model.addAttribute("workers", workers);
        model.addAttribute("category", category);
        return "Appointments/select_worker";
    }

    @GetMapping("/calendar/{id}")
    public String showCalendar(@PathVariable Long id,  Model model, HttpServletRequest request, @RequestParam(name = "date", required = false) String dateStr) {
        String token = (String) request.getSession().getAttribute("sessionToken");

        LocalDate date = (dateStr == null) ? LocalDate.now() : LocalDate.parse(dateStr);
        model.addAttribute("currentDate", date);
        model.addAttribute("workerId", id);

        // Get all appointments for this worker to find busy times
        List<AppointmentDTO> workerAppointments = appointmentClient.getWorkerAppointments(id, token);

        // Filter appointments for the selected date
        List<AppointmentDTO> appointmentsForDate = workerAppointments.stream()
                .filter(a -> a.getStartTime().toLocalDate().isEqual(date))
                .collect(Collectors.toList());

        // Generate available time slots
        List<LocalTime> availableTimes = generateTimeSlots(appointmentsForDate);
        model.addAttribute("availableTimes", availableTimes);

        return "Appointments/calendar";
    }

    @PostMapping("/book")
    public String bookAppointment(@ModelAttribute AppointmentDTO appointmentDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");

        // The DTO has workerId and serviceId set from the form.
        // The user ID should be set on the backend based on the authenticated user.

        // For demonstration, let's assume we pass a full DTO.
        // The `startTime` is passed from the calendar form.

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

    // Helper method to generate time slots
    private List<LocalTime> generateTimeSlots(List<AppointmentDTO> bookedAppointments) {
        List<LocalTime> allTimes = new ArrayList<>();
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(18, 0);

        while (start.isBefore(end)) {
            allTimes.add(start);
            start = start.plus(30, ChronoUnit.MINUTES);
        }

        // Remove booked times
        for (AppointmentDTO booked : bookedAppointments) {
            LocalTime bookedStart = booked.getStartTime().toLocalTime();
            LocalTime bookedEnd = booked.getEndTime().toLocalTime();
            allTimes.removeIf(time -> !time.isBefore(bookedStart) && time.isBefore(bookedEnd));
        }

        return allTimes;
    }
}

