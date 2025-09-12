package com.example.MB_beauty_club_frontend.controllers;

import com.example.MB_beauty_club_frontend.clients.ShoppingCartClient;
import com.example.MB_beauty_club_frontend.dtos.CartItemDTO;
import com.example.MB_beauty_club_frontend.session.SessionManager;
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

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartClient shoppingCartClient;

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (token == null) {
            return "redirect:/auth/login?message=loginRequired";
        }

        try {
            shoppingCartClient.addToCart(id, 1, token);
        } catch (Exception e) {
            log.error("Failed to add item to cart: {}", e.getMessage());
        }

        return "redirect:/shopping-cart";
    }

    @GetMapping
    public String showCart(Model model, HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (token == null) {
            return "redirect:/auth/login?message=loginRequired";
        }

        List<CartItemDTO> cartItems = shoppingCartClient.showCart(token);

        BigDecimal totalLeva = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEuro = cartItems.stream()
                .map(item -> item.getProduct().getEuroPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Добавяне на общата сума към модела
        model.addAttribute("totalLeva", totalLeva);
        model.addAttribute("totalEuro", totalEuro);
        model.addAttribute("cartItems", cartItems);
        return "ShoppingCart/show";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable Long id, HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        shoppingCartClient.removeCartItem(id, token);
        return "redirect:/shopping-cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long cartItemId, @RequestParam int quantity, HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        shoppingCartClient.updateQuantity(cartItemId, quantity, token);
        return "redirect:/shopping-cart";
    }
}
