package com.example.JWT.user.dto;

import com.example.JWT.domain.user.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegisterDto {
    private String userEmail;

    private String userPassword;

    private String userName;

    public Users toEntity(){
        return Users.builder()
                .username(userName)
                .email(userEmail)
                .password(userPassword)
                .build();
    }
}
