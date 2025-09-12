package com.example.MB_beauty_club_frontend.dtos;

import com.example.MB_beauty_club_frontend.dtos.auth.PublicUserDTO;
import com.example.MB_beauty_club_frontend.enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableDTO {

    public UUID id;
    private int number;
    private int capacity;
    private TableStatus status;
    private OrderDTO currentOrder;
    private PublicUserDTO waiter;

}
