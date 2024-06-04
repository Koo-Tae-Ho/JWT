package com.example.JWT.user.dto;

import com.example.JWT.domain.user.Users;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private String userEmail;
    private String userName;

    public UserResponseDto(Users users){
        this.userEmail = users.getUserEmail();
        this.userName = users.getUsername();
    }
}