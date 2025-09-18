package com.example.MB_beauty_club_frontend.clients;

import com.example.MB_beauty_club_frontend.dtos.CartItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "MB-shopping-cart", url = "${backend.base-url}/shopping-cart")
public interface ShoppingCartClient {

    @PostMapping("/addToCart")
    void addToCart(@RequestParam(value = "productId") Long productId, @RequestParam("quantity") int quantity, @RequestHeader("Authorization") String auth);

    @PostMapping("/addToCartByBarcode")
    void addToCartByBarcode(@RequestParam(value = "barcode") String barcode, @RequestParam("quantity") int quantity, @RequestHeader("Authorization") String auth);

    @GetMapping("/showCart")
    List<CartItemDTO> showCart(@RequestHeader("Authorization") String auth);

    @PostMapping("/removeCartItem")
    void removeCartItem(@RequestParam("cartItemId") Long cartItemId, @RequestHeader("Authorization") String auth);

    @PutMapping("/updateQuantity")
    void updateQuantity(@RequestParam("cartItemId") Long cartItemId, @RequestParam int quantity, @RequestHeader("Authorization") String auth);

}
