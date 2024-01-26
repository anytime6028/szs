package com.project.szs.Auth.Service;


import com.project.szs.Auth.DTO.UserDto;
import com.project.szs.Auth.Entity.User;
import com.project.szs.Auth.Repository.UserRepositoty;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepositoty userRepositoty;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signup(UserDto userDto)
    {
//        if(userRepositoty.findB)
    }





}
