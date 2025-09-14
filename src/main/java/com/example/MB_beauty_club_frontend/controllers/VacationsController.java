package com.example.MB_beauty_club_frontend.controllers;

import com.example.MB_beauty_club_frontend.clients.VacationsClient;
import com.example.MB_beauty_club_frontend.dtos.VacationDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/vacations")
public class VacationsController {

    private final VacationsClient vacationClient;

    @GetMapping
    public String showVacations(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || (!userRole.equals("WORKER"))) {
            return "redirect:/";
        }

        List<VacationDTO> vacations = vacationClient.getMyVacations(token);
        model.addAttribute("vacations", vacations);

        return "Vacations/show";
    }

    @GetMapping("/edit")
    public String getVacationForm(Model model, HttpServletRequest request, @RequestParam(name = "success", required = false) String success) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("WORKER")) {
            return "redirect:/";
        }

        List<VacationDTO> existingVacations = vacationClient.getMyVacations(token);
        model.addAttribute("vacations", existingVacations);
        model.addAttribute("newVacation", new VacationDTO());
        model.addAttribute("success", success != null);

        return "Vacations/edit";
    }

    @PostMapping("/add")
    public String addVacation(@ModelAttribute VacationDTO vacationDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("WORKER")) {
            return "redirect:/";
        }

        vacationClient.setVacation(vacationDTO, token);
        return "redirect:/vacations/edit?success=true";
    }

    @PostMapping("/delete/{id}")
    public String deleteVacation(@PathVariable Long id, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("WORKER")) {
            return "redirect:/";
        }

        vacationClient.deleteVacation(id, token);
        return "redirect:/vacations/edit";
    }
}