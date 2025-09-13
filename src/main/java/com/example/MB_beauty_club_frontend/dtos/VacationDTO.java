package com.example.MB_beauty_club_frontend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacationDTO {

    private Long id;
    private WorkerDTO worker;
    private LocalDate startDate;
    private LocalDate endDate;
}
