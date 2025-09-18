package com.example.MB_beauty_club_frontend.dtos;

import com.example.MB_beauty_club_frontend.dtos.auth.PublicUserDTO;
import com.example.MB_beauty_club_frontend.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

    private UUID id;
    private PublicUserDTO user;
    private LocalDate orderDate;
    private boolean deleted;
    private boolean invoiced;
    private OrderStatus status;
    private BigDecimal price;
    private BigDecimal euroPrice;

}
