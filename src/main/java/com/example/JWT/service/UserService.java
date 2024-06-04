package com.example.JWT.service;

import com.example.JWT.domain.jwt.Jwt;
import com.example.JWT.domain.jwt.JwtUtils;
import com.example.JWT.domain.user.*;
import com.example.JWT.filter.AuthenticateUser;
import com.example.JWT.filter.JwtLoginFilter;
import com.example.JWT.user.dto.UserLoginDto;
import com.example.JWT.user.dto.UserRegisterDto;
import com.example.JWT.user.dto.UserResponseDto;
import com.example.JWT.user.dto.UserVerifyResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        if(userRepository.findByUserEmail("admin123@naver.com") == null) {
            Users users = Users.builder()
                    .email("admin123@naver.com")
                    .username("koo")
                    .password("admin12345")
                    .build();

            UserRole userRole = UserRole.builder()
                    .role(Role.ADMIN)
                    .user(users)
                    .build();

            userRepository.save(users);
            userRoleRepository.save(userRole);
        }
    }

    @Transactional
    public UserResponseDto registerUser(UserRegisterDto userRegisterDto){
        Users user = userRepository.save(userRegisterDto.toEntity());
        UserRole role = UserRole.builder()
                .role(Role.USER)
                .user(user)
                .build();
        user.addRole(role);
        userRoleRepository.save(role);
        return new UserResponseDto(user);
    }


    //User가 실제 있는 유저인지 검증
    public UserVerifyResponseDto verifyUser(UserLoginDto userLoginDto){
        Users user = userRepository.findByUserEmail(userLoginDto.getUserEmail());
        if(user == null || !userLoginDto.getUserPassword().equals(user.getPassword())) {
            return UserVerifyResponseDto.builder()
                    .isValid(false)
                    .build();
        }

        return UserVerifyResponseDto.builder()
                .isValid(true)
                .userRole(user.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toSet())).build();
    }

    public UserResponseDto findUserByEmail(String userEmail){
        Users users = userRepository.findByUserEmail(userEmail);
        if(users == null) return null;
        return new UserResponseDto(users);
    }

    @Transactional
    public void updateRefreshToken(String userEmail,String refreshToken){
        Users user = userRepository.findByUserEmail(userEmail);
        if(user == null)
            return;
        user.updateRefreshToken(refreshToken);
    }

    public Jwt createAccessToken(String refreshToken) {
        try{
            // 유효한 토큰 인지 검증
            jwtUtils.getClaims(refreshToken);
            Users user = userRepository.findByRefreshToken(refreshToken);

            if(user == null)
                return null;

            HashMap<String, Object> claims = new HashMap<>();
            AuthenticateUser authenticateUser = new AuthenticateUser(user.getUserEmail(),
                    user.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toSet()));
            String authenticateUserJson = objectMapper.writeValueAsString(authenticateUser);
            claims.put(JwtLoginFilter.AUTHENTICATE_USER,authenticateUserJson);
            Jwt jwt = jwtUtils.createAccessJwt(claims);
            return jwt;
        }catch (Exception e){
            return null;
        }
    }

    @Transactional
    public Jwt refreshToken(String refreshToken){
        try{

            // 유효한 토큰 인지 검증
            jwtUtils.getClaims(refreshToken);
            Users user = userRepository.findByRefreshToken(refreshToken);
            if(user == null)
                return null;

            HashMap<String, Object> claims = new HashMap<>();
            AuthenticateUser authenticateUser = new AuthenticateUser(user.getUserEmail(),
                    user.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toSet()));
            String authenticateUserJson = objectMapper.writeValueAsString(authenticateUser);
            claims.put(JwtLoginFilter.AUTHENTICATE_USER,authenticateUserJson);
            Jwt jwt = jwtUtils.createJwt(claims);
            updateRefreshToken(user.getUserEmail(),jwt.getRefreshToken());
            return jwt;
        } catch (Exception e){
            return null;
        }
    }

    @Transactional
    public boolean addUserRole(String email, Role role){
        Users users = userRepository.findByUserEmail(email);
        if(users.getUserRoles().stream().anyMatch(userRole -> userRole.getRole().equals(role)))
            return false;
        UserRole userRole = UserRole.builder()
                .user(users)
                .role(role)
                .build();
        users.addRole(userRole);
        userRoleRepository.save(userRole);
        return true;
    }

}
