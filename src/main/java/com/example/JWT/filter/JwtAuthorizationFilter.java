package com.example.JWT.filter;

import com.example.JWT.auth.dto.AuthorizationException;
import com.example.JWT.domain.jwt.Jwt;
import com.example.JWT.domain.jwt.JwtUtils;
import com.example.JWT.domain.user.Role;
import com.example.JWT.domain.user.UserRepository;
import com.example.JWT.domain.user.UserRole;
import com.example.JWT.domain.user.Users;
import com.example.JWT.service.UserService;
import com.example.JWT.user.dto.UserResponseDto;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements Filter {
    private final Map<String, List<Role>> authorizationMap;
    private final UserRepository userRepository;

    private final String[] whiteListUris = new String[]{
            "/home/home.html",
            "/team4/signup",
            "/css/**",
            "/js/**",
            "/html/**",
            "/team4/login",
            "/auth/access/token",
            "/user/login",
            "/auth/refresh/token",
            "/user/register",
            "*/h2-console*",
            "/team4/logout",
            "/team4/register"
    };

    private final JwtUtils jwtUtils;

    private final ObjectMapper objectMapper;

    private final UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if(whiteListCheck(httpServletRequest.getRequestURI())){
            chain.doFilter(request, response);
            return;
        }

        if(!isContainTokenForSSR(httpServletRequest)){
            httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(),"인증 오류");
            return;
        }
        try{
            String token = getTokenForSSR(httpServletRequest);
            AuthenticateUser authenticateUser = getAuthenticateUser(token);
            String userEmail = authenticateUser.getEmail();
            UserResponseDto user = userService.findUserByEmail(userEmail);

            if(user == null) {
                httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "유저 정보를 찾을 수 없습니다.");
            }


            boolean isAuthorizedUser = verifyAuthorization(httpServletRequest, authenticateUser);
            if(!isAuthorizedUser) {
                httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다.");
            }
            else {
                request.setAttribute("authentication", authenticateUser);
                chain.doFilter(request, response);
            }

        } catch (JsonParseException e){
            log.error("JsonParseException");
            request.setAttribute("Exception", e);
            RequestDispatcher rd = request.getRequestDispatcher("/team4/login");
            rd.forward(request, response);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException e){
            log.error("JwtException");
            request.setAttribute("Exception", e);
            RequestDispatcher rd = request.getRequestDispatcher("/team4/login");
            rd.forward(request, response);
        } catch (ExpiredJwtException e){
            //엑세스토큰 만료시,
            log.error("JwtTokenExpired");
            request.setAttribute("Exception", e);

            //refresh토큰 가져오기
            String refreshToken = "";
            for(Cookie c : ((HttpServletRequest) request).getCookies()) {
                if(c.getName().equals("refresh_token")) {
                    refreshToken = c.getValue();
                    break;
                }
            }

            //refresh토큰을 통해 새로운 access토큰 발급
            Jwt jwt = userService.createAccessToken(refreshToken);
            Users user = userRepository.findByRefreshToken(refreshToken);

            Set<Role> roleSet = new HashSet<>();
            for(UserRole ur : user.getUserRoles()) {
                roleSet.add(ur.getRole());
            }

            //인증객체 생성
            AuthenticateUser authenticateUser = new AuthenticateUser(user.getUserEmail(), roleSet);

            //===================================== 액세스 토큰 재발급 =======================================

            jwtUtils.addJwtToHttpOnlyForSSR(jwt.getAccessToken(), refreshToken, (HttpServletRequest) request, (HttpServletResponse) response);

            boolean isAuthorizedUser = verifyAuthorization(httpServletRequest, authenticateUser);
            if(!isAuthorizedUser) {
                httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다.");
            } else {
                request.setAttribute("authentication", authenticateUser);
                chain.doFilter(request, response);
            }

            //==================================== 액세스 토큰 발급 X =========================================

//            request.setAttribute("Exception", e);
//            RequestDispatcher rd = request.getRequestDispatcher("/team4/login");
//            rd.forward(request, response);

        } catch (AuthorizationException e){
            log.error("AuthorizationException");
            request.setAttribute("Exception", e);
            RequestDispatcher rd = request.getRequestDispatcher("/team4/login");
            rd.forward(request, response);
        }
    }

    private boolean whiteListCheck(String uri){
        return PatternMatchUtils.simpleMatch(whiteListUris,uri);
    }

    private boolean isContainToken(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        return authorization != null && authorization.startsWith("Bearer");
    }

    private String getToken(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        return authorization.substring(7);
    }

    private boolean isContainTokenForSSR(HttpServletRequest request){
        for(Cookie c : request.getCookies()) {
            if(c.getName().equals("access_token")) {
                return true;
            }
        }
        return false;
    }

    private String getTokenForSSR(HttpServletRequest request){
       for(Cookie c : request.getCookies()) {
           if(c.getName().equals("access_token")) {
               return c.getValue().substring(6);
           }
       }
       return null;
    }

    private AuthenticateUser getAuthenticateUser(String token) throws JsonProcessingException {
        Claims claims = jwtUtils.getClaims(token);
        String authenticateUserJson = claims.get(JwtLoginFilter.AUTHENTICATE_USER, String.class);
        return objectMapper.readValue(authenticateUserJson, AuthenticateUser.class);
    }

    //사용자 권한확인
    private boolean verifyAuthorization(HttpServletRequest httpServletRequest, AuthenticateUser authenticateUser){
        boolean isAuthorizedUser = false;

        for(Role role : authenticateUser.getRoles()) {
            String uri = httpServletRequest.getRequestURI();
            if(authorizationMap.get(uri).contains(role)) {
                isAuthorizedUser = true;
            }
            else {
                isAuthorizedUser = false;}
        }
        return isAuthorizedUser;
    }
}