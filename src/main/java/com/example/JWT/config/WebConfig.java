package com.example.JWT.config;

import com.example.JWT.domain.jwt.JwtUtils;
import com.example.JWT.domain.user.Role;
import com.example.JWT.domain.user.UserRepository;
import com.example.JWT.filter.JwtAuthorizationFilter;
import com.example.JWT.filter.JwtFilter;
import com.example.JWT.filter.JwtLoginFilter;
import com.example.JWT.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class WebConfig {

    //로그인필터
    @Bean
    public FilterRegistrationBean jwtLoginFilter(ObjectMapper mapper, UserService userService) {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtLoginFilter(mapper,userService));
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/user/login");
        return filterRegistrationBean;
    }

    //로그인필터 다음 호출되는 jwt 생성필터
    @Bean
    public FilterRegistrationBean jwtFilter(JwtUtils provider, ObjectMapper mapper, UserService userService) {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtFilter(provider,mapper,userService));
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/user/login");
        return filterRegistrationBean;
    }

    //로그인이 완료된 사용자의 인가필터
    @Bean
    public FilterRegistrationBean jwtAuthorizationFilter(JwtUtils provider, ObjectMapper mapper, UserService userService, UserRepository userRepository) {
        Map<String, List<Role>> authorizationMap = new HashMap<>();
        authorizationMap.put("/team4/user_admin", new ArrayList<>(Collections.arrayToList(new Role[]{Role.ADMIN, Role.USER})));
        authorizationMap.put("/team4/admin", new ArrayList<>(Collections.arrayToList(new Role[]{Role.ADMIN})));
        authorizationMap.put("/team4/home", new ArrayList<>(Collections.arrayToList(new Role[]{Role.ADMIN, Role.USER})));
        authorizationMap.put("/auth/access/token", new ArrayList<>(Collections.arrayToList(new Role[]{Role.ADMIN, Role.USER})));
        authorizationMap.put("/auth/refresh/token", new ArrayList<>(Collections.arrayToList(new Role[]{Role.ADMIN, Role.USER})));
        authorizationMap.put("/auth/admin", new ArrayList<>(Collections.arrayToList(new Role[]{Role.ADMIN})));
        authorizationMap.put("/favicon.ico", new ArrayList<>(Collections.arrayToList(new Role[]{Role.ADMIN, Role.USER})));

        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtAuthorizationFilter(authorizationMap,userRepository ,provider, mapper, userService));
        filterRegistrationBean.setOrder(2);
        return filterRegistrationBean;
    }
}