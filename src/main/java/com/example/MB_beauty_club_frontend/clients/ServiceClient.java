package com.example.MB_beauty_club_frontend.clients;


import com.example.MB_beauty_club_frontend.dtos.ServiceDTO;
import com.example.MB_beauty_club_frontend.enums.WorkerCategory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "MB-services", url = "${backend.base-url}/services")
public interface ServiceClient {

    @GetMapping
    List<ServiceDTO> getAllServices(@RequestHeader("Authorization") String auth);

    @GetMapping("/byCategory")
    List<ServiceDTO> getServicesByCategory(@RequestParam("category") WorkerCategory category, @RequestHeader(value = "Authorization", required = false) String auth);

    @GetMapping("/byId")
    ServiceDTO getServiceById(@RequestParam("id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping("/add")
    void addService(@RequestBody ServiceDTO serviceDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/update")
    void updateService(@RequestBody ServiceDTO serviceDTO, @RequestHeader("Authorization") String auth);

    @PostMapping("/delete")
    void deleteService(@RequestParam("id") Long id, @RequestHeader("Authorization") String auth);

}
