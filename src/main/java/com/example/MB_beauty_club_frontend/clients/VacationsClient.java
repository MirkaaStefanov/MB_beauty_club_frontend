package com.example.MB_beauty_club_frontend.clients;

import com.example.MB_beauty_club_frontend.dtos.VacationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "MB-vacations", url = "${backend.base-url}/vacations")
public interface VacationsClient {

    @GetMapping
    List<VacationDTO> getMyVacations(@RequestHeader("Authorization") String auth);

    @PutMapping
    void setVacation(@RequestBody VacationDTO newVacation, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    void deleteVacation(@PathVariable Long id, @RequestHeader("Authorization") String auth);


}
