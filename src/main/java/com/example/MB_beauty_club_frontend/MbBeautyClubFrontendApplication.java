package com.example.MB_beauty_club_frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MbBeautyClubFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MbBeautyClubFrontendApplication.class, args);
	}

}
