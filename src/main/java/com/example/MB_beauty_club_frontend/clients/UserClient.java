package com.example.MB_beauty_club_frontend.clients;

import com.example.MB_beauty_club_frontend.dtos.auth.PublicUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "MB-users", url = "${backend.base-url}/users")
public interface UserClient {

    @GetMapping("/me")
    PublicUserDTO getMe(@RequestHeader("Authorization") String auth);

}
