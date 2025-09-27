package com.example.MB_beauty_club_frontend.clients;

import com.example.MB_beauty_club_frontend.dtos.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "MB-payments", url = "${backend.base-url}/payments")
public interface PaymentClient {

    @PostMapping("/create-session")
    String createCheckoutSession(@RequestBody PaymentRequest request, @RequestHeader("Authorization") String auth);


}
