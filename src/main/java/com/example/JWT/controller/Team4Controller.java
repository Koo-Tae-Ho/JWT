package com.example.JWT.controller;

import com.example.JWT.domain.user.UserRepository;
import com.example.JWT.domain.user.UserRole;
import com.example.JWT.domain.user.UserRoleRepository;
import com.example.JWT.domain.user.Users;
import com.example.JWT.filter.AuthenticateUser;
import com.example.JWT.service.UserService;
import com.example.JWT.user.dto.UserRegisterDto;
import com.example.JWT.user.dto.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/team4")
public class Team4Controller {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public Team4Controller(UserService userService, UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @GetMapping("/login")
    public String login(Model model , HttpServletRequest request) {
        if(request.getAttribute("Exception") != null) {
            model.addAttribute("Exception", ((Exception)request.getAttribute("Exception")).getMessage());
        }
        return "login/login";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model) {
        AuthenticateUser authenticateUser = (AuthenticateUser) request.getAttribute("authentication");
        String userEmail = authenticateUser.getEmail();
        model.addAttribute("userEmail",userEmail);
        return "home/home";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        response.addHeader("Set-Cookie", "access_token= ; " +
                "Path=/; " +
                "Domain=ec2-3-38-210-153.ap-northeast-2.compute.amazonaws.com; " +
                "HttpOnly; " +
                "Max-Age=0; ");
        return "redirect:/team4/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "sign/signup";
    }

    @PostMapping("/register")
    public String register(@Valid  @ModelAttribute UserRegisterDto userRegisterDto){
        userService.registerUser(userRegisterDto);
        return "login/login";
    }
}
