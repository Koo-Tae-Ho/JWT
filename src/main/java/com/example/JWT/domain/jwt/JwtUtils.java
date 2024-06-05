package com.example.JWT.domain.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    public static final byte[] secret = "KosaSecretKeyKosaSecretKeyKosaSecretKey".getBytes();
    private final Key key = Keys.hmacShaKeyFor(secret);

    //토큰 생성
    public String createToken(Map<String, Object> claims, Date expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    //Claims 얻어오기
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Jwt createJwt(Map<String, Object> claims) {
        String accessToken = "Bearer" + createToken(claims, getExpireDateAccessToken());
        String refreshToken = createToken(new HashMap<>(), getExpireDateRefreshToken());
        return Jwt.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //jwt토큰 생성
    public String[] createJwtForSSR(Map<String, Object> claims) {
        String accessToken = "Bearer" + createToken(claims, getExpireDateAccessToken());
        String refreshToken = createToken(new HashMap<>(), getExpireDateRefreshToken());
        return new String[] {accessToken, refreshToken};
    }

    //access토큰 발급
    public Jwt createAccessJwt(Map<String, Object> claims) {
        String accessToken = "Bearer" + createToken(claims, getExpireDateAccessToken());
        return Jwt.builder()
                .accessToken(accessToken)
                .build();
    }

    //access토큰 만료시간 설정
    public Date getExpireDateAccessToken() {
        //(1초에 1000밀리초)
        long expireTimeMils = 1000 * 5; //현재 5초로 설정
        return new Date(System.currentTimeMillis() + expireTimeMils);
    }

    //refresh토큰 만료시간 설정
    public Date getExpireDateRefreshToken() {
        long expireTimeMils = 1000L * 60 * 60 * 24; //24시간
        return new Date(System.currentTimeMillis() + expireTimeMils);
    }

    //HTTP only 쿠키에 토큰 저장
    public void addJwtToHttpOnlyForSSR(String accessToken, String refreshToken, HttpServletResponse response) {
        response.addHeader("Set-Cookie", "access_token=" + accessToken + " ; " +
                                                "Path=/; " +
                                                "Domain=ec2-3-38-210-153.ap-northeast-2.compute.amazonaws.com; " +
                                                "HttpOnly; " +
                                                "Max-Age=3600000; " +
                                                "SameSite=None; " +
                                                "Secure; ");

        response.addHeader("Set-Cookie", "refresh_token=" + refreshToken + " ; " +
                "Path=/; " +
                "Domain=ec2-3-38-210-153.ap-northeast-2.compute.amazonaws.com; " +
                "HttpOnly; " +
                "Max-Age=3600000; " +
                "SameSite=None; " +
                "Secure; ");

    }

//    // HTTP only 쿠키에 토큰 저장, 도메인 동적 설정
//    public void addJwtToHttpOnlyForSSR(String accessToken, String refreshToken, HttpServletRequest request, HttpServletResponse response) {
//        String domain = "3.38.210.153";  // 사용자가 접속한 서버의 이름을 가져옵니다.
//
//        String cookieSettings = String.format("Path=/; Domain=%s; HttpOnly; Max-Age=3600000; SameSite=None; Secure; ", domain);
//
//        response.addHeader("Set-Cookie", "access_token=" + accessToken + " ; " + cookieSettings);
//        response.addHeader("Set-Cookie", "refresh_token=" + refreshToken + " ; " + cookieSettings);
//    }

}
