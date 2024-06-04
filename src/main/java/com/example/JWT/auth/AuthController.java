package com.example.JWT.auth;

import com.example.JWT.auth.dto.TokenRefreshDto;
import com.example.JWT.domain.jwt.Jwt;
import com.example.JWT.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @GetMapping("/admin")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<Jwt> tokenRefresh(@RequestBody TokenRefreshDto tokenRefreshDto) {
        Jwt jwt = userService.refreshToken(tokenRefreshDto.getRefreshToken());

        //jwt가 null이면, 401코드 반환
        if (jwt == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        //jwt반환
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/access/token")
    public ResponseEntity<Jwt> tokenAccess(@RequestBody TokenRefreshDto tokenRefreshDto) {
        Jwt jwt = userService.createAccessToken(tokenRefreshDto.getRefreshToken());
        //jwt가 null이면, 401코드 반환
        if (jwt == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        //jwt반환
        return ResponseEntity.ok(jwt);
    }
}
