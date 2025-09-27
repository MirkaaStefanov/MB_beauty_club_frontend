package com.example.MB_beauty_club_frontend.controllers;

import com.example.MB_beauty_club_frontend.clients.OrderClient;
import com.example.MB_beauty_club_frontend.clients.PaymentClient;
import com.example.MB_beauty_club_frontend.dtos.OrderDTO;
import com.example.MB_beauty_club_frontend.dtos.OrderProductDTO;
import com.example.MB_beauty_club_frontend.dtos.PaymentRequest;
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
import org.springframework.web.servlet.view.RedirectView;

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
    private final PaymentClient paymentClient;

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
    public RedirectView createOrder(HttpServletRequest request, RedirectAttributes redirectAttributes) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        // 1. Authentication Check
        if (role == null || (!"ADMIN".equals(role) && !"USER".equals(role))) {
            // Redirect to home/login page if unauthorized
            return new RedirectView("/");
        }

        OrderDTO orderDTO = null; // Declare here for access in the catch block

        try {
            // 2. Create Order (Stock is reserved, status is PENDING)
            orderDTO = orderClient.createOrder(token);
            List<OrderProductDTO> orderProductDTOS = orderClient.findOrderProductsForOrder(orderDTO.getId(), token);

            if(!"ADMIN".equals(role)){
                PaymentRequest paymentRequest = new PaymentRequest();
                paymentRequest.setOrderId(orderDTO.getId());
                paymentRequest.setItems(orderProductDTOS);
                paymentRequest.setTotalAmount(orderDTO.getPrice());

                // 4. Get the Stripe Checkout Session URL
                String stripeCheckoutUrl = paymentClient.createCheckoutSession(paymentRequest, token);

                // 5. Success: Redirect the user directly to the external Stripe URL
                return new RedirectView(stripeCheckoutUrl);
            }


            return new RedirectView("/orders");


        } catch (InsufficientStockException e) {
            // 6. Handle Insufficient Stock Error
            log.error("Insufficient stock to create order: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Redirect back to the shopping cart
            return new RedirectView("/shopping-cart");

        } catch (Exception e) {
            // 7. Handle General Error (e.g., failed API call, network issue)
            log.error("Failed to create new order or start payment: {}", e.getMessage());

            // CRITICAL: Attempt to cancel the PENDING order and restock if the error
            // happened *after* the order was created but *before* Stripe redirect.
            if (orderDTO != null) {
                try {
                    log.warn("Attempting to cancel pending order {} due to payment initiation failure.", orderDTO.getId());
                    orderClient.cancelOrder(orderDTO.getId(), token);
                } catch (Exception cancelException) {
                    log.error("FATAL: Failed to cancel order {}. Stock may be locked.", orderDTO.getId(), cancelException);
                }
            }

            redirectAttributes.addFlashAttribute("errorMessage", "Възникна грешка при стартиране на плащането. Моля, опитайте отново.");
            // Redirect back to the shopping cart
            return new RedirectView("/shopping-cart");
        }
    }

    @GetMapping("/payment-cancel/{id}")
    public String paymentCancel(@PathVariable UUID id, HttpServletRequest request){
        String token = (String) request.getSession().getAttribute("sessionToken");
        orderClient.cancelOrder(id,token);
        return "redirect:/shopping-cart";
    }

    @GetMapping("/payment-success/{id}")
    public String paymentSuccess(@PathVariable UUID id, HttpServletRequest request){
        String token = (String) request.getSession().getAttribute("sessionToken");
        orderClient.successPayment(id,token);
        return "redirect:/orders";
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
