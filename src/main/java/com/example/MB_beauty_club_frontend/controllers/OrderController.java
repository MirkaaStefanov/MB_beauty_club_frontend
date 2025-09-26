package com.example.MB_beauty_club_frontend.controllers;

import com.example.MB_beauty_club_frontend.clients.OrderClient;
import com.example.MB_beauty_club_frontend.dtos.OrderDTO;
import com.example.MB_beauty_club_frontend.dtos.OrderProductDTO;
import com.example.MB_beauty_club_frontend.enums.OrderStatus;
import com.example.MB_beauty_club_frontend.exception.InsufficientStockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/orders")
public class OrderController {

    private final OrderClient orderClient;

    @GetMapping
    public String getMyOrders(Model model, HttpServletRequest request, @RequestParam(required = false) String status) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        List<OrderDTO> orders = new ArrayList<>();

        if ("WORKER".equals(role)) {
            return "redirect:/";
        }

        try {
            if ("ADMIN".equals(role)) {
                orders = orderClient.getAllOrders(token);
            } else {
                orders = orderClient.getAllOrdersForAuthenticatedUser(token);
            }

            // --- Filter logic based on the 'status' parameter ---
            String filterStatus = (status == null || status.isEmpty()) ? "PENDING" : status;

            orders = orders.stream()
                    .filter(order -> order.getStatus().name().equals(filterStatus))
                    .collect(Collectors.toList());

            // --- Sorting logic based on the filtered status ---
            if ("PENDING".equals(filterStatus) || "DELIVER".equals(filterStatus)) {
                // Sort by order date, oldest first (ascending)
                orders.sort(Comparator.comparing(OrderDTO::getOrderDate));
            } else if ("DONE".equals(filterStatus)) {
                // Sort by order date, newest first (descending)
                orders.sort(Comparator.comparing(OrderDTO::getOrderDate).reversed());
            }

            model.addAttribute("statuses", OrderStatus.values());
            model.addAttribute("orders", orders);
            model.addAttribute("currentStatus", status);
            return "Orders/my-orders";
        } catch (Exception e) {
            log.error("Failed to fetch orders: {}", e.getMessage());
            model.addAttribute("errorMessage", "Възникна грешка при зареждането на поръчките.");
            return "Orders/my-orders";
        }
    }

    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable UUID id, Model model, HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        if ("WORKER".equals(role) || token == null) {
            return "redirect:/";
        }

        try {
            OrderDTO order = orderClient.getOrderById(id, token);
            List<OrderProductDTO> orderProducts = orderClient.findOrderProductsForOrder(id, token);

            model.addAttribute("order", order);
            model.addAttribute("orderProducts", orderProducts);
        } catch (Exception e) {
            log.error("Failed to fetch details for order with ID {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", "Възникна грешка при зареждането на детайлите за поръчката.");
            return "redirect:/orders"; // Redirect if details cannot be loaded
        }

        return "Orders/details";
    }

    @PostMapping("/create")
    public String createOrder(HttpServletRequest request, RedirectAttributes redirectAttributes) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        if (role == null || (!"ADMIN".equals(role) && !"USER".equals(role))) {
            return "redirect:/";
        }


        try {
            orderClient.createOrder(token);
            // On success, redirect to the user's orders page
            return "redirect:/orders";
        } catch (InsufficientStockException e) {
            // Catch the specific stock error and add a flash attribute
            log.error("Insufficient stock to create order: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/shopping-cart"; // Redirect back to the cart
        } catch (Exception e) {
            // Catch any other unexpected errors
            log.error("Failed to create new order: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Възникна грешка при създаването на поръчката.");
            return "redirect:/shopping-cart";
        }
    }

    @PostMapping("/update/{id}")
    public String updateStatus(@PathVariable UUID id, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(role)) {
            return "redirect:/";
        }

        orderClient.updateStatus(id, token);
        return "redirect:/orders/" + id;

    }
}
