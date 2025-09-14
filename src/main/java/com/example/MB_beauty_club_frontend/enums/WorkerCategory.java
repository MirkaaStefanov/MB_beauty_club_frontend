package com.example.MB_beauty_club_frontend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum WorkerCategory {

    HAIRSTYLING("Подстригване"),
    NAIL("Маникюр и Педикюр"),
    MAKEUP("Грим"),
    LASH("Миглопластика"),
    MASSAGE("Масаж");

    private final String description;


}
