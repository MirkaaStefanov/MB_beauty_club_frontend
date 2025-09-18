package com.example.MB_beauty_club_frontend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    private Long id;
    private ProductDTO product;
    private int quantity;
    private BigDecimal price;
    private BigDecimal euroPrice;
    private boolean deleted;
    private ShoppingCartDTO shoppingCart;
}
