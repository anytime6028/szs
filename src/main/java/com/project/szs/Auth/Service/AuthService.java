package com.project.szs.Auth.Service;


import com.project.szs.Auth.DTO.LoginDto;
import com.project.szs.Auth.DTO.MemberDto;
import com.project.szs.Auth.Entity.Member;
import com.project.szs.Auth.Repository.MemberRepositoty;
import com.project.szs.JWT.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepositoty memberRepositoty;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    Map<String, String> sampleMember = Map.of(
                "홍길동","860824-1655068",
                "김둘리","921108-1582816",
                "마징가","880601-2455116",
                "베지터","910411-1656116",
                "손오공","820326-2715702");




    @Transactional
    public String signup(MemberDto memberDto)
    {
        if(sampleMember.containsKey(memberDto.getName()))
        {
            if(sampleMember.get(memberDto.getName()).equals(memberDto.getRegNo()))
            {
                if (memberRepositoty.findUserByName(memberDto.getName()).isPresent()) {
                    return "fail";
                }

                Member member = Member.builder()
                        .userId(memberDto.getUserId())
                        .password(encrypt(memberDto.getPassword()))
                        .name(memberDto.getName())
                        .regNo(encrypt(memberDto.getRegNo()))
                        .build();

                memberRepositoty.save(member);

                return "ok11";
            }
            else
            {
                return " incorrect regno";
            }
        }
        else
        {
            return " incorrect name ";
        }
    }


    public Map<String, Object> login(LoginDto loginDto)
    {
        Map<String, Object> map = new HashMap<>();

       Member member = memberRepositoty.findUserByUserId(loginDto.getUserId());

        if(matchBcrypt(loginDto.getPassword(), member.getPassword()))
        {
            String token = tokenProvider.createToken(member);

            tokenProvider.SetAuthentication(token);

            map.put("accessToken",token);

            return map;
        }
        map.put("notokok","notok");
        return map;
    }















    public String encrypt(String str)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(5);

        return encoder.encode(str);
    }

    public Boolean matchBcrypt(String str, String hashStr)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(5);

        return encoder.matches(str,hashStr);
    }





}
