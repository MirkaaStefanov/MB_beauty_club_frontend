package com.example.MB_beauty_club_frontend.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "MB-exchange", url = "${backend.base-url}/exchange")
public interface ExchangeClient {

    @GetMapping("/try")
    Double exchangeEuroToTRY();

}
