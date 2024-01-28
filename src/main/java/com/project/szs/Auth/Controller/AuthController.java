package com.project.szs.Auth.Controller;


import com.project.szs.Auth.DTO.TokenDto;
import com.project.szs.Auth.DTO.UserDto;
import com.project.szs.Auth.Entity.User;
import com.project.szs.Auth.Service.AuthService;
import com.project.szs.JWT.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/szs")
@RequiredArgsConstructor
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthService auth;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto)
    {
        return ResponseEntity.ok(auth.signup(userDto));
    }



}
