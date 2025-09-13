package com.example.MB_beauty_club_frontend.controllers;

import com.example.MB_beauty_club_frontend.clients.ServiceClient;
import com.example.MB_beauty_club_frontend.dtos.ServiceDTO;
import com.example.MB_beauty_club_frontend.enums.WorkerCategory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/services")
public class ServicesController {

    private final ServiceClient serviceClient;

    @GetMapping
    public String showAllServices(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");

        List<ServiceDTO> services = serviceClient.getAllServices(token);
        model.addAttribute("services", services);

        return "Services/show";
    }

    @GetMapping("/edit")
    public String getServicesForm(Model model, HttpServletRequest request, @RequestParam(name = "id", required = false) Long id) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("ADMIN")) {
            return "forward:/error";
        }

        List<ServiceDTO> existingServices = serviceClient.getAllServices(token);
        model.addAttribute("services", existingServices);
        model.addAttribute("categories", Arrays.asList(WorkerCategory.values()));

        if (id != null) {
            ServiceDTO serviceToEdit = serviceClient.getServiceById(id, token);
            model.addAttribute("service", serviceToEdit);
        } else {
            model.addAttribute("service", new ServiceDTO());
        }

        return "Services/edit";
    }

    @PostMapping("/save")
    public String saveService(@ModelAttribute("service") ServiceDTO serviceDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("ADMIN")) {
            return "forward:/error";
        }

        if (serviceDTO.getId() != null) {
            serviceClient.updateService(serviceDTO, token);
        } else {
            serviceClient.addService(serviceDTO, token);
        }

        return "redirect:/services/edit";
    }

    @PostMapping("/delete")
    public String deleteService(@RequestParam("id") Long id, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("ADMIN")) {
            return "forward:/error";
        }

        serviceClient.deleteService(id, token);
        return "redirect:/services/edit";
    }
}

