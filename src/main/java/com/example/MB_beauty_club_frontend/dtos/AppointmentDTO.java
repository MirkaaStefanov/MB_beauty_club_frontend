package com.example.MB_beauty_club_frontend.dtos;

import com.example.MB_beauty_club_frontend.dtos.auth.PublicUserDTO;
import com.example.MB_beauty_club_frontend.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDTO {

    private Long id;
    private WorkerDTO worker;
    private PublicUserDTO user;
    private ServiceDTO service;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
}
