package com.example.MB_beauty_club_frontend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("В очакване", "bg-orange-100 text-orange-800"),
    DELIVER("За доставка", "bg-blue-100 text-blue-800"),
    DONE("Изпълнена", "bg-green-100 text-green-800");

    private final String displayName;
    private final String cssClass;
}