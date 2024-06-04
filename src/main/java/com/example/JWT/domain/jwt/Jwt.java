package com.example.JWT.domain.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Jwt {
    private String accessToken; // 엑세스 토큰
    private String refreshToken; // 리프레시 토큰

    @Builder
    public Jwt(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
