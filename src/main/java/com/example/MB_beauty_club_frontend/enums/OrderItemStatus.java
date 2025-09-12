package com.example.MB_beauty_club_frontend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderItemStatus {

    WAITING("За одобрение"),
    PREPARING("Приготвя се"),
    DONE("Готово");

    private final String displayName;

}
