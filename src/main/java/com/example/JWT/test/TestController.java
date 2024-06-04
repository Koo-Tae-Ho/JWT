package com.example.JWT.test;

import com.example.JWT.filter.AuthenticateUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/team4")
public class TestController {

//    @GetMapping("/user_admin")
//    public ResponseEntity<String> user_admin(HttpServletRequest request) {
//        return new ResponseEntity(((AuthenticateUser)request.getAttribute("authentication")).getRoles() +"인증 성공.", HttpStatus.OK);
//    }

    @GetMapping("/user_admin")
    public String user_admin() {
        return "service/user_admin";
    }

    @GetMapping("/admin")
    public String admin() {
        return "service/admin";
    }


//    @GetMapping("/admin")
//    @ResponseBody
//    public ResponseEntity<String> admin(HttpServletRequest request) {
//        return new ResponseEntity(((AuthenticateUser)request.getAttribute("authentication")).getRoles() +"인증 성공.", HttpStatus.OK);
//    }

    @GetMapping("/accessToken")
    @ResponseBody
    public ResponseEntity<String> accessToken(HttpServletRequest request) {
        return new ResponseEntity(((AuthenticateUser)request.getAttribute("authentication")).getRoles() +"인증 성공.", HttpStatus.OK);
    }

}
