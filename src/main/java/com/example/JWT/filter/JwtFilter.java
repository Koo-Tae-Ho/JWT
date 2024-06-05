package com.example.JWT.filter;

import com.example.JWT.domain.jwt.Jwt;
import com.example.JWT.domain.jwt.JwtUtils;
import com.example.JWT.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtils jwtUtils;

    private final ObjectMapper objectMapper;

    private final UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {

        Object attribute = request.getAttribute(JwtLoginFilter.AUTHENTICATE_USER);
        if (attribute instanceof AuthenticateUser authenticateUser) {

            //claims 생성
            Map<String, Object> claims = new HashMap<>();
            String authenticateUserJson = objectMapper.writeValueAsString(authenticateUser);

            //claims에 인증객체 포함
            claims.put(JwtLoginFilter.AUTHENTICATE_USER, authenticateUserJson);

            //jwt 생성 및 리프레시토큰 DB저장
            String[] tokens = jwtUtils.createJwtForSSR(claims);
            jwtUtils.addJwtToHttpOnlyForSSR(tokens[0], tokens[1], (HttpServletResponse) response);
            userService.updateRefreshToken(authenticateUser.getEmail(), tokens[1]);

            response.getWriter().write("success");
            return;
        }

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
