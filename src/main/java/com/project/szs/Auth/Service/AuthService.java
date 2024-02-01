package com.project.szs.Auth.Service;


import com.project.szs.Auth.DTO.LoginDto;
import com.project.szs.Auth.DTO.MemberDto;
import com.project.szs.Auth.Entity.AmountInfo;
import com.project.szs.Auth.Entity.Member;
import com.project.szs.Auth.Repository.AmountInfoRepository;
import com.project.szs.Auth.Repository.MemberRepositoty;
import com.project.szs.JWT.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepositoty memberRepositoty;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AmountInfoRepository amountInfoRepository;

    @Value("${key.secret}")
    private String key;
    Map<String, String> sampleMember = Map.of(
                "홍길동","860824-1655068",
                "김둘리","921108-1582816",
                "마징가","880601-2455116",
                "베지터","910411-1656116",
                "손오공","820326-2715702");




    @Transactional
    public Map<String, Object> signup(MemberDto memberDto) throws Exception
    {
        Map<String, Object> response = new HashMap<>();
        if(sampleMember.containsKey(memberDto.getName()))
        {
            if(sampleMember.get(memberDto.getName()).equals(memberDto.getRegNo()))
            {
                if (memberRepositoty.findUserByName(memberDto.getName()).isPresent() || memberRepositoty.findUserByUserId(memberDto.getUserId()).isPresent()) {
                    response.put("result", "fail");
                    return response;
                }

                Member member = Member.builder()
                        .userId(memberDto.getUserId())
                        .password(encrypt(memberDto.getPassword()))
                        .name(memberDto.getName())
                        .regNo(regNoEncrypt(memberDto.getRegNo()))
                        .build();

                memberRepositoty.save(member);
                response.put("result", "complete");
                return response;
            }
            else
            {
                response.put("result", "incorrect regno");
                return response;
            }
        }
        else
        {
            response.put("result", "incorrect name");
            return response;
        }
    }


    public Map<String, Object> login(LoginDto loginDto)
    {
        Map<String, Object> map = new HashMap<>();

       Member member = memberRepositoty.findMemberByUserId(loginDto.getUserId());

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


    public String scrap() throws Exception
    {
        Long memberId = tokenProvider.getMemberIdFromToken();
        Member member = memberRepositoty.getById(memberId);

        String uri = "https://codetest.3o3.co.kr/v2/scrap";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        Map<String, Object> map = new HashMap<>();
        map.put("name", member.getName());
        map.put("regNo", regNoDecrypt(member.getRegNo()));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(uri, requestEntity, Map.class);

//        int statusCode = responseEntity.getStatusCodeValue();
//        System.out.println("Response Code: " + statusCode);

        Map responseBody = responseEntity.getBody();
        System.out.println("Response Body: " + responseBody);

        try {
            JSONObject jsonList = (JSONObject) new JSONObject(responseBody).getJSONObject("data").getJSONObject("jsonList");
            JSONArray paymentArr = (JSONArray) jsonList.getJSONArray("급여");
            JSONArray taxDeductionArr = (JSONArray) jsonList.getJSONArray("소득공제");
            int totalPayment = 0; // 총급여
            double taxAmount = 0; //산출세액
            int insurance = 0; //소득공제-보험료
            int education = 0; //소득공제-교육비
            int donate = 0; //소득공제-기부금
            int medical = 0; //소득공제-의료비
            double retirementPension = 0; //소득공제-의료비

            for (Object obj : paymentArr)
            {
                JSONObject paymentObj = (JSONObject) obj;

                if(paymentObj.get("소득내역").equals("급여"))
                {
                    totalPayment += converAmountToInt(paymentObj.get("총지급액").toString());
                }
            }

            taxAmount = converAmountToDouble(jsonList.get("산출세액").toString());

            for (Object obj : taxDeductionArr )
            {
                JSONObject taxDeductionObj = (JSONObject) obj;

                switch (taxDeductionObj.get("소득구분").toString())
                {
                    case "보험료":
                        insurance += converAmountToInt(taxDeductionObj.getString("금액")) ;
                        break;
                    case "교육비":
                        education += converAmountToInt(taxDeductionObj.getString("금액")) ;
                        break;
                    case "기부금":
                        donate += converAmountToInt(taxDeductionObj.getString("금액")) ;
                        break;
                    case "의료비":
                        medical += converAmountToInt(taxDeductionObj.getString("금액")) ;
                        break;
                    case "퇴직연금":
                        retirementPension += converAmountToDouble(taxDeductionObj.getString("총납임금액")) ;
                        break;
                }
            }

            AmountInfo amountInfo = AmountInfo.builder()
                    .memberId(memberId)
                    .totalPayment(totalPayment)
                    .taxAmount(taxAmount)
                    .insurance(insurance)
                    .education(education)
                    .donate(donate)
                    .medical(medical)
                    .retirementPension(retirementPension)
                    .build();

            amountInfoRepository.save(amountInfo);

        }catch (Exception e)
        {
            e.printStackTrace();
        }

//        System.out.println("payment test: " + payment.);
        return "ok";
    }


    public Map<String, Object> refund() throws Exception
    {
        Map<String, Object> response = new HashMap<>();
        Long memberId = tokenProvider.getMemberIdFromToken();
        Member member = memberRepositoty.getById(memberId);

        if(amountInfoRepository.getAmountInfoByMemberId(memberId).isEmpty())
        {
            scrap();
        }
            AmountInfo amountInfo = amountInfoRepository.findAmountInfoByMemberId(memberId);

            double retirementDeduction = amountInfo.getRetirementPension() * 0.15; //퇴직연금세액공제금액
            double determinedTax = 0; //결정세액
            double specialDeductionAmount = 0;
            //보험료 공제금액
            double specialInsurance = amountInfo.getInsurance()*0.12;
            //교육비 공제금액
            double specialEducation = amountInfo.getEducation()*0.15;
            //기부금 공제금액
            double specialDonate = amountInfo.getDonate()*0.15;
            //의료비 공제금액
            double specialMedical = Math.max((amountInfo.getMedical() - (amountInfo.getTotalPayment()*0.03)) *0.15,0);

            specialDeductionAmount = specialInsurance + specialMedical + specialEducation + specialDonate;

            double standardDeductionAmount = 0; //표준세액공제금액

            if(specialDeductionAmount < 130000)
            {
                standardDeductionAmount = 130000;
                specialDeductionAmount = 0;
            }

            if(amountInfo != null)
            {
                determinedTax = amountInfo.getTaxAmount() - (amountInfo.getTaxAmount()*0.55)
                                - specialDeductionAmount - standardDeductionAmount - retirementDeduction;

            }

            response.put("이름", member.getName());
            response.put("결정세액", reFormat(Math.max(determinedTax,0)));
            response.put("퇴직연금세액공제", reFormat(retirementDeduction));

        return response;
    }






    public int converAmountToInt(Object amount)
    {
        return Integer.valueOf(amount.toString().replace(",",""));

    }

    public Double converAmountToDouble(Object amount)
    {
        return Double.valueOf(amount.toString().replace(",",""));

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


    private String regNoEncrypt(String plaintext) throws Exception {

        SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String regNoDecrypt(String ciphertext) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public String reFormat(double target)
    {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.###");
        String formatted = decimalFormat.format(target);

        return formatted;
    }


}
