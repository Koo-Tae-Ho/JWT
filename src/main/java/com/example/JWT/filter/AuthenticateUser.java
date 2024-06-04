package com.example.JWT.filter;

import com.example.JWT.domain.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

//인증객체
@Getter
@AllArgsConstructor
public class AuthenticateUser {
    private String email;
    private Set<Role> roles;
}
