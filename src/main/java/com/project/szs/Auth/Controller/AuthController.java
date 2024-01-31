package com.project.szs.Auth.Controller;


import com.project.szs.Auth.DTO.LoginDto;
import com.project.szs.Auth.DTO.MemberDto;
import com.project.szs.Auth.Service.AuthService;
import com.project.szs.JWT.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/szs")
@RequiredArgsConstructor
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthService auth;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberDto memberDto) throws Exception
    {
        return ResponseEntity.ok(auth.signup(memberDto));
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto)
    {
        return ResponseEntity.ok(auth.login(loginDto));
    }


    @PostMapping("scrap")
    public ResponseEntity<?> scrap() throws Exception
    {
        return ResponseEntity.ok(auth.scrap());
    }

    @PostMapping("refund")
    public ResponseEntity<?> refund() throws Exception
    {
        return ResponseEntity.ok(auth.refund());
    }

    @PostMapping("test")
    public void test()
    {
        auth.test();
    }




}
