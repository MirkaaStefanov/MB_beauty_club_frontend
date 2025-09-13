package com.example.MB_beauty_club_frontend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingHoursWrapperDTO {
    private List<WorkingHoursDTO> workingHours;
}
