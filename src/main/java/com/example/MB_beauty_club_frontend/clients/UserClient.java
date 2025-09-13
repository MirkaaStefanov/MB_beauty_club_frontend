package com.example.MB_beauty_club_frontend.clients;

import com.example.MB_beauty_club_frontend.dtos.auth.PublicUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "MB-users", url = "${backend.base-url}/users")
public interface UserClient {

    @GetMapping("/me")
    PublicUserDTO getMe(@RequestHeader("Authorization") String auth);

    @GetMapping("/all")
    List<PublicUserDTO> getAllUsers(@RequestHeader("Authorization") String auth);


}
