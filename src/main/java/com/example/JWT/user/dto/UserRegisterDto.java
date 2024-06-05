package com.example.JWT.user.dto;

import com.example.JWT.domain.user.Users;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegisterDto {
    @NotEmpty
    private String userEmail;
    @NotEmpty
    private String userPassword;
    @NotEmpty
    private String userName;

    public Users toEntity(){
        return Users.builder()
                .username(userName)
                .email(userEmail)
                .password(userPassword)
                .build();
    }
}
