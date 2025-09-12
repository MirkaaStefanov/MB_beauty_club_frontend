package com.example.MB_beauty_club_frontend.dtos;


import com.example.MB_beauty_club_frontend.dtos.auth.PublicUserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingCartDTO {

    private Long id;
    private PublicUserDTO user;
    private boolean deleted;

}
