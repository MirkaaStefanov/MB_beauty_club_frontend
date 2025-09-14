package com.example.MB_beauty_club_frontend.controllers;


import com.example.MB_beauty_club_frontend.clients.WorkerClient;
import com.example.MB_beauty_club_frontend.dtos.WorkerDTO;
import com.example.MB_beauty_club_frontend.enums.WorkerCategory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
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
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/workers")
public class WorkerController {

    private final WorkerClient workerClient;

    @GetMapping("/{userId}")
    public String selectRole(@PathVariable UUID userId, Model model, HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("ADMIN")) {
            return "redirect:/";
        }

        model.addAttribute("id", userId);
        model.addAttribute("categories", WorkerCategory.values());

        return "Worker/select";
    }

    @PostMapping
    public String create(@RequestParam UUID userId, WorkerCategory category, HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("ADMIN")) {
            return "redirect:/";
        }

        workerClient.create(userId, category, token);
        return "redirect:/users";
    }

    @GetMapping
    public String all(Model model, HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("ADMIN")) {
            return "redirect:/";
        }

        List<WorkerDTO> all = workerClient.all(token);
        model.addAttribute("allWorkers", all);
        return "Worker/all";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, HttpServletRequest request, Model model) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("ADMIN")) {
            return "redirect:/";
        }

        WorkerDTO workerDTO = workerClient.findById(id, token);
        model.addAttribute("id", id);
        model.addAttribute("update", workerDTO);
        model.addAttribute("categories", WorkerCategory.values());
        return "Worker/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, @ModelAttribute WorkerDTO workerDTO, HttpServletRequest request, Model model) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("ADMIN")) {
            return "redirect:/";
        }

        workerClient.update(id, workerDTO, token);
        return "redirect:/workers";
    }
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, HttpServletRequest request){
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("ADMIN")) {
            return "redirect:/";
        }

        workerClient.delete(id, token);
        return "redirect:/workers";
    }


}
