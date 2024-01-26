package com.project.szs.Auth.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NonNull
    private String userId;     // 로그인 ID

    @NonNull
    private String password;    // 로그인 비밀번호

}
