package com.project.szs.Auth.Service;


import com.project.szs.Auth.DTO.UserDto;
import com.project.szs.Auth.Entity.User;
import com.project.szs.Auth.Repository.MemberRepositoty;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepositoty userRepositoty;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String signup(UserDto userDto)
    {
        if(userRepositoty.findUserByUserId(userDto.getUserId()).isPresent())
        {
            return "fail";
        }

        User user = User.builder()
                .userId(userDto.getUserId())
                .password(userDto.getPassword())
                .name(userDto.getName())
                .regNo(userDto.getRegNo())
                .build();

        return "ok11";
    }





}
