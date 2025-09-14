package com.example.MB_beauty_club_frontend.clients;

import com.example.MB_beauty_club_frontend.dtos.WorkingHoursDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "MB-working-hours", url = "${backend.base-url}/working-hours")
public interface WorkingHoursClient {

    @GetMapping
    List<WorkingHoursDTO> getMyWorkingHours(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    List<WorkingHoursDTO> getWorkingHoursByWorkerId(@PathVariable UUID id, @RequestHeader(value = "Authorization", required = false) String auth);

    @PutMapping
    void setWorkingHours(@RequestBody List<WorkingHoursDTO> newHours, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id, @RequestHeader("Authorization") String auth);

}
