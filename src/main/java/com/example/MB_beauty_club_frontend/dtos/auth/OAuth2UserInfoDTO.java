package com.example.MB_beauty_club_frontend.dtos.auth;



import com.example.MB_beauty_club_frontend.dtos.common.BaseDTO;
import com.example.MB_beauty_club_frontend.enums.Provider;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OAuth2UserInfoDTO extends BaseDTO {
    private String sub;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String email;
    private boolean email_verified;
    private String locale;
    private Provider provider = Provider.LOCAL;
}