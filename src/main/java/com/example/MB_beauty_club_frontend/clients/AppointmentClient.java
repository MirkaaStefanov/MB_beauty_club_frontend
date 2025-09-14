package com.example.MB_beauty_club_frontend.clients;

import com.example.MB_beauty_club_frontend.dtos.AppointmentDTO;
import com.example.MB_beauty_club_frontend.enums.AppointmentStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "MB-appointments", url = "${backend.base-url}/appointments")
public interface AppointmentClient {

    @GetMapping("/my-appointments")
    List<AppointmentDTO> getMyAppointments(@RequestHeader("Authorization") String auth);

    @GetMapping("/worker-appointments/{id}")
    List<AppointmentDTO> getWorkerAppointments(@PathVariable UUID id, @RequestHeader(value = "Authorization", required = false) String auth);

    @GetMapping("/pending-worker-appointments")
    List<AppointmentDTO> getPendingWorkerAppointments(@RequestHeader("Authorization") String auth);

    @PostMapping("/book")
    void bookAppointment(@RequestBody AppointmentDTO appointmentDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/update-status/{id}")
    AppointmentDTO updateAppointmentStatus(@PathVariable("id") Long id, @RequestParam("status") AppointmentStatus status, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    void deleteAppointment(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

}
