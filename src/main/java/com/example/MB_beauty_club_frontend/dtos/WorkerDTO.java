package com.example.MB_beauty_club_frontend.dtos;

import com.example.MB_beauty_club_frontend.dtos.auth.PublicUserDTO;
import com.example.MB_beauty_club_frontend.enums.WorkerCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkerDTO {

    private UUID id;

    private PublicUserDTO user;
    private String name;
    private String email;
    private WorkerCategory workerCategory;


}
