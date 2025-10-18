package com.example.MB_beauty_club_frontend.dtos.auth;



import com.example.MB_beauty_club_frontend.enums.Provider;
import com.example.MB_beauty_club_frontend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String surName;
    private Role role = Role.USER;
    private Provider provider = Provider.LOCAL;
    private String phoneNumber;

}
