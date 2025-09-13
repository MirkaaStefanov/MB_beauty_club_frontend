package com.example.MB_beauty_club_frontend.controllers;

import com.example.MB_beauty_club_frontend.clients.AppointmentClient;
import com.example.MB_beauty_club_frontend.clients.ProductClient;
import com.example.MB_beauty_club_frontend.dtos.AppointmentDTO;
import com.example.MB_beauty_club_frontend.dtos.ProductDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final ProductClient productClient;
    private final AppointmentClient appointmentClient;

    @GetMapping("/")
    public String homePage(Model model, HttpServletRequest request){

        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        if ("ADMIN".equals(role)){
            return "redirect:/products";
        }
        if ("WORKER".equals(role)){
            return "redirect:/appointments/my-appointments";
        }
        List<ProductDTO> allProducts = productClient.getAllProducts(true, null, token);

        // Вземете само първите 3 продукта, които са налични за продажба
        List<ProductDTO> featuredProducts = allProducts.stream()
                .limit(3)
                .collect(Collectors.toList());

        model.addAttribute("featuredProducts", featuredProducts);

        return "index";
    }
}
