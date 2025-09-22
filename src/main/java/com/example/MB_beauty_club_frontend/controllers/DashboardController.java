package com.example.MB_beauty_club_frontend.controllers;

import com.example.MB_beauty_club_frontend.clients.OrderClient;
import com.example.MB_beauty_club_frontend.dtos.OrderDTO;
import com.example.MB_beauty_club_frontend.dtos.OrderProductDTO;
import com.example.MB_beauty_club_frontend.dtos.ProductDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/dashboard")
public class DashboardController {

    private final OrderClient orderClient;

    @GetMapping
    public String dashboard(Model model, HttpServletRequest request){

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(userRole)) {
            return "redirect:/";
        }

        List<OrderDTO> allOrders = orderClient.getAllOrders(token);
        List<OrderProductDTO> allOrderProducts = orderClient.allOrderProducts(token);
        model.addAttribute("orders", allOrders);
        model.addAttribute("orderProducts", allOrderProducts);
        return "dashboard";
    }
}

