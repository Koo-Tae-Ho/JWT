package com.example.JWT.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginDto {
    private String userEmail;
    private String userPassword;
}