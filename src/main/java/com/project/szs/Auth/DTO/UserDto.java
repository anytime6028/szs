package com.project.szs.Auth.DTO;


import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {


    @NonNull
    private String userId;     // 로그인 ID

    @NonNull
    private String password;    // 로그인 비밀번호

    @NonNull
    private String name;    // 유저실명

    @NonNull
    private String regNo;       // 주민등록번호


}
