package com.project.szs.Auth.Controller;


import com.project.szs.Auth.DTO.LoginDto;
import com.project.szs.Auth.DTO.MemberDto;
import com.project.szs.Auth.Service.AuthService;
import com.project.szs.JWT.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> signup(@RequestBody MemberDto memberDto)
    {
        return ResponseEntity.ok(auth.signup(memberDto));
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto)
    {
        return ResponseEntity.ok(auth.login(loginDto));
    }


    @PostMapping("checktoken")
    public ResponseEntity<?> check()
    {
        return ResponseEntity.ok("ok");
    }



}
