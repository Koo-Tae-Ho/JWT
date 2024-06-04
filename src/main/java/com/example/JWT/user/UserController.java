package com.example.JWT.user;

import com.example.JWT.service.UserService;
import com.example.JWT.user.dto.UserRegisterDto;
import com.example.JWT.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterDto userRegisterDto){
        return new ResponseEntity(userService.registerUser(userRegisterDto), HttpStatus.CREATED);
    }
}