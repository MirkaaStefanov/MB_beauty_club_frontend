package com.example.MB_beauty_club_frontend.controllers;

import com.example.MB_beauty_club_frontend.clients.WorkingHoursClient;
import com.example.MB_beauty_club_frontend.dtos.WorkingHoursDTO;
import com.example.MB_beauty_club_frontend.dtos.WorkingHoursWrapperDTO;
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

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/working-hours")
public class WorkingHoursController {

    private final WorkingHoursClient workingHoursClient;

    @GetMapping
    public String showWorkingHours(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || (!userRole.equals("WORKER") && !userRole.equals("ADMIN"))) {
            return "forward:/error";
        }

        List<WorkingHoursDTO> existingHours = workingHoursClient.getMyWorkingHours(token);
        Map<DayOfWeek, WorkingHoursDTO> existingHoursMap = existingHours.stream()
                .collect(Collectors.toMap(WorkingHoursDTO::getDayOfWeek, dto -> dto));

        List<WorkingHoursDTO> allDaysList = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            if (existingHoursMap.containsKey(day)) {
                allDaysList.add(existingHoursMap.get(day));
            } else {
                WorkingHoursDTO emptyHour = new WorkingHoursDTO();
                emptyHour.setDayOfWeek(day);
                allDaysList.add(emptyHour);
            }
        }

        model.addAttribute("workingHours", allDaysList);
        return "WorkingHours/show";
    }

    @GetMapping("/edit")
    public String getWorkingHours(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("WORKER")) {
            return "forward:/error";
        }

        List<WorkingHoursDTO> existingHours = workingHoursClient.getMyWorkingHours(token);

        Map<DayOfWeek, WorkingHoursDTO> existingHoursMap = existingHours.stream()
                .collect(Collectors.toMap(WorkingHoursDTO::getDayOfWeek, dto -> dto));

        List<WorkingHoursDTO> allDaysList = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            if (existingHoursMap.containsKey(day)) {
                allDaysList.add(existingHoursMap.get(day));
            } else {
                WorkingHoursDTO emptyHour = new WorkingHoursDTO();
                emptyHour.setDayOfWeek(day);
                allDaysList.add(emptyHour);
            }
        }

        WorkingHoursWrapperDTO wrapperDTO = new WorkingHoursWrapperDTO();
        wrapperDTO.setWorkingHours(allDaysList);
        model.addAttribute("workingHoursWrapper", wrapperDTO);

        return "WorkingHours/edit";
    }

    @PostMapping("/edit")
    public String setWorkingHours(@ModelAttribute WorkingHoursWrapperDTO workingHoursWrapper, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("WORKER")) {
            return "forward:/error";
        }

        List<WorkingHoursDTO> filteredHours = workingHoursWrapper.getWorkingHours().stream()
                .filter(wh -> wh.getStartTime() != null &&
                        wh.getEndTime() != null)
                .collect(Collectors.toList());

        workingHoursClient.setWorkingHours(filteredHours, token);
        return "redirect:/working-hours/edit?success=true";
    }

    @PostMapping("/{id}")
    public String delete(@PathVariable Long id, HttpServletRequest request){
        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (userRole == null || !userRole.equals("WORKER")) {
            return "forward:/error";
        }

        workingHoursClient.delete(id,token); // Pass the token to the client
        return "redirect:/working-hours";
    }

}
