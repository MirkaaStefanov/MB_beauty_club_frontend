package com.example.MB_beauty_club_frontend.dtos;


import com.example.MB_beauty_club_frontend.enums.WorkerCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceDTO {

    private Long id;
    private WorkerCategory category;
    private String name;
    private String description;
    private Double price;
    private Integer duration;

}
