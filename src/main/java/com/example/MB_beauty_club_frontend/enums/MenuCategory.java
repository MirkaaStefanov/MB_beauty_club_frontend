package com.example.MB_beauty_club_frontend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuCategory {
    APPETIZER("Предястия"),
    MAIN_COURSE("Основни ястия"),
    DESSERT("Десерти"),
    BEVERAGE("Напитки"),
    SIDE("Други");

    private final String displayName;
}
