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
public class OrderProductDTO {

    private Long id;
    private Integer number;
    private ProductDTO product;
    private OrderDTO order;
    private boolean deleted;
    private BigDecimal sellingPrice;

}
