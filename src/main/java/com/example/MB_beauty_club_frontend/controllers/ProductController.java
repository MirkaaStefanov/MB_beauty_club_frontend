package com.example.MB_beauty_club_frontend.controllers;

import com.example.MB_beauty_club_frontend.clients.ProductClient;
import com.example.MB_beauty_club_frontend.dtos.NeedProductDTO;
import com.example.MB_beauty_club_frontend.dtos.ProductDTO;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/products")
public class ProductController {

    private final ProductClient productClient;


    @GetMapping
    public String getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean forSale,
            Model model,
            HttpServletRequest request) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if ("ADMIN".equals(userRole)) {
            List<ProductDTO> allForAdmin = productClient.getAllProducts(null, null, token);
            model.addAttribute("products", allForAdmin);
            return "Product/allADMIN";
        }
        // Изтеглете всички продукти, които са за продажба
        List<ProductDTO> allProducts = productClient.getAllProducts(true, null, token);
        model.addAttribute("products", allProducts);
        return "Product/all";
    }

    @GetMapping("/create")
    public String createMenuItem(
            HttpServletRequest request, Model model) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(userRole)) {
            return "redirect:/";
        }

        model.addAttribute("product", new ProductDTO());

        return "Product/form";
    }


    @PostMapping("/create")
    public String createMenuItem(
            @ModelAttribute ProductDTO productDTO,// Matches form input name
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(role)) {
            return "redirect:/";
        }

        try {
            if (productDTO.getImageFile() != null && !productDTO.getImageFile().isEmpty()) {
                byte[] fileBytes = productDTO.getImageFile().getBytes();
                String encodedImage = Base64.getEncoder().encodeToString(fileBytes);
                productDTO.setImage(encodedImage);
            } else if (productDTO.getImage() != null && !productDTO.getImage().isEmpty()) {

            } else {
                productDTO.setImage(null);
                productDTO.setImageFile(null);
            }

            productClient.create(productDTO, token);
            redirectAttributes.addFlashAttribute("successMessage", "Menu item created successfully!");
        } catch (Exception e) {
            log.error("Error creating menu item: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create menu item: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String createMenuItem(@PathVariable Long id,
            HttpServletRequest request, Model model) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(userRole)) {
            return "redirect:/";
        }

        ProductDTO productDTO = productClient.getById(id, token);
        model.addAttribute("product", productDTO);

        return "Product/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateSubmit(@PathVariable Long id, @ModelAttribute ProductDTO productDTO, HttpServletRequest request) throws IOException {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");
        if (!"ADMIN".equals(role)) {
            return "redirect:/";
        }

        ProductDTO existingProduct = productClient.getById(id, token);

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setProductCategory(productDTO.getProductCategory());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setComingPrice(productDTO.getComingPrice());
        existingProduct.setAvailableQuantity(productDTO.getAvailableQuantity());
        existingProduct.setBarcode(productDTO.getBarcode());
        existingProduct.setForSale(productDTO.isForSale());

        if (productDTO.getImageFile() != null && !productDTO.getImageFile().isEmpty()) {
            byte[] fileBytes = productDTO.getImageFile().getBytes();
            String encodedImage = Base64.getEncoder().encodeToString(fileBytes);
            existingProduct.setImage(encodedImage);
        }
        productClient.update(id, existingProduct, token);
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ProductDTO getMenuItemById(@PathVariable Long id, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        return productClient.getById(id, token);
    }


    @PostMapping("/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(role)) {
            return "redirect:/";
        }

        try {
            productClient.delete(id, token);
            redirectAttributes.addFlashAttribute("successMessage", "Menu item deleted successfully!");
        } catch (Exception e) {
            log.error("Error deleting menu item with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete menu item: " + e.getMessage());
        }
        return "redirect:/products";
    }


    @PostMapping("/toggle/{id}")
    public String toggleAvailability(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(role)) {
            return "redirect:/";
        }
        try {
            productClient.toggleAvailability(id, token);
            redirectAttributes.addFlashAttribute("successMessage", "Menu item availability toggled!");
        } catch (Exception e) {
            log.error("Error toggling availability for menu item ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to toggle availability: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/promote/{id}")
    public String promoteProduct(
            @PathVariable Long id,
            HttpServletRequest request,
            Model model) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(userRole)) {
            return "redirect:/";
        }

        ProductDTO productDTO = productClient.getById(id, token);
        model.addAttribute("product", productDTO);

        return "Product/promote";
    }

    @PostMapping("/{id}/promote")
    public String createPromotion(
            @PathVariable Long id,
            @RequestParam int percent,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(role)) {
            return "redirect:/";
        }

        try {
            productClient.createPromotion(id, percent, token);
            redirectAttributes.addFlashAttribute("successMessage", "Product promotion created successfully!");
        } catch (Exception e) {
            log.error("Error creating promotion for product ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create promotion: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/{id}/remove-promote")
    public String deletePromotion(
            @PathVariable Long id,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(role)) {
            return "redirect:/";
        }

        try {
            productClient.deletePromotion(id, token);
            redirectAttributes.addFlashAttribute("successMessage", "Product promotion removed successfully!");
        } catch (Exception e) {
            log.error("Error deleting promotion for product ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove promotion: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/restock/{id}")
    public String restock(
            @PathVariable Long id,
            HttpServletRequest request,
            Model model) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String userRole = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(userRole)) {
            return "redirect:/";
        }

        ProductDTO productDTO = productClient.getById(id, token);
        model.addAttribute("product", productDTO);

        return "Product/restock";
    }


    @PostMapping("/{id}/restock")
    public String restock(
            @PathVariable Long id,
            @RequestParam int quantity,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(role)) {
            return "redirect:/";
        }

        try {
            productClient.restock(id, quantity, token);
            redirectAttributes.addFlashAttribute("successMessage", "Product promotion created successfully!");
        } catch (Exception e) {
            log.error("Error creating promotion for product ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create promotion: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/need-products")
    public String needProducts(HttpServletRequest request, Model model){
        String token = (String) request.getSession().getAttribute("sessionToken");
        String role = (String) request.getSession().getAttribute("sessionRole");

        if (!"ADMIN".equals(role)) {
            return "redirect:/";
        }

        List<NeedProductDTO> needProducts = productClient.getAllNeedProducts(token);
        model.addAttribute("needProducts", needProducts);
        return "Product/need-products";
    }
}
