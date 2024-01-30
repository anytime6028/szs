package com.project.szs.Auth.Service;


import com.project.szs.Auth.DTO.LoginDto;
import com.project.szs.Auth.DTO.MemberDto;
import com.project.szs.Auth.Entity.Member;
import com.project.szs.Auth.Repository.MemberRepositoty;
import com.project.szs.JWT.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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


    public String scrap()
    {
        String uri = "https://codetest.3o3.co.kr/v2/scrap";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        Map<String, Object> map = new HashMap<>();
        map.put("name", "홍길동");
        map.put("regNo", "860824-1655068");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(uri, requestEntity, Map.class);

//        int statusCode = responseEntity.getStatusCodeValue();
//        System.out.println("Response Code: " + statusCode);

        Map responseBody = responseEntity.getBody();
        System.out.println("Response Body: " + responseBody);

        try {
            JSONObject datajobj = (JSONObject) new JSONObject(responseBody).get("data");
            JSONArray paymentArr = (JSONArray) datajobj.getJSONObject("jsonList").getJSONArray("급여");

            int totalPayment = 0; // 총급여
            int taxAmount = 0; //산출세액


            for (Object obj : paymentArr)
            {
                JSONObject paymentObj = (JSONObject) obj;

                if(paymentObj.get("소득내역").equals("급여"))
                {
                    totalPayment += converAmountToInt(paymentObj.get("총지급액").toString());
                }
            }
            System.out.println("총지급액 : " + totalPayment);

        }catch (Exception e)
        {
            e.printStackTrace();
        }

//        System.out.println("payment test: " + payment.);
        return "ok";
    }










    public int converAmountToInt(String amount)
    {
        return Integer.valueOf(amount.replace(",",""));

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
