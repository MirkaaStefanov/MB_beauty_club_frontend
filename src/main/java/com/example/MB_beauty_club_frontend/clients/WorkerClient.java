package com.example.MB_beauty_club_frontend.clients;


import com.example.MB_beauty_club_frontend.dtos.WorkerDTO;
import com.example.MB_beauty_club_frontend.enums.WorkerCategory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "MB-worker", url = "${backend.base-url}/workers")
public interface WorkerClient {

    @PostMapping
    WorkerDTO create(@RequestParam UUID userId, @RequestParam WorkerCategory category, @RequestHeader(value = "Authorization", required = false) String auth);

    @GetMapping
    List<WorkerDTO> all(@RequestHeader(value = "Authorization", required = false) String auth);

    @PutMapping
    WorkerDTO update(@RequestParam UUID id, @RequestBody WorkerDTO workerDTO, @RequestHeader(value = "Authorization", required = false) String auth);

    @PostMapping("/delete")
    void delete(@RequestParam UUID id, @RequestHeader(value = "Authorization", required = false) String auth);

    @GetMapping("/{id}")
    WorkerDTO findById(@PathVariable UUID id, @RequestHeader(value = "Authorization", required = false) String auth);

    @GetMapping("/byCategory")
    List<WorkerDTO> getWorkersByCategory(@RequestParam("category") WorkerCategory category, @RequestHeader("Authorization") String auth);

}
