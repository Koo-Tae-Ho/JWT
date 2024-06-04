package com.example.JWT.user.dto;

import com.example.JWT.domain.user.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class UserVerifyResponseDto {
    private boolean isValid;
    private Set<Role> userRole;
}