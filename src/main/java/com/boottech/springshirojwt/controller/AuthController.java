package com.boottech.springshirojwt.controller;


import com.boottech.springshirojwt.dto.LoginRequest;
import com.boottech.springshirojwt.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(req.username(), req.password());

        try {
            // Authenticate user with Shiro
            subject.login(token);
            String username = (String) subject.getPrincipal();
            // Generate JWT
            return JwtUtil.generateToken(username);

        } catch (AuthenticationException e) {
            throw new AuthenticationException("Invalid credentials");
        }
    }
}
