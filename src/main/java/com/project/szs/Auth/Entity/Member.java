package com.project.szs.Auth.Entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;


@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
@Getter
@Setter
@Builder
@Entity
public class Member {

//    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long memberId;

    @Column(name = "userId", nullable = false)
    private String userId;     // 로그인 ID

    @Column(name = "password", nullable = false)
    private String password;    // 로그인 비밀번호

    @Column(name = "name", nullable = false)
    private String name;    // 유저실명

    @Column(name = "regNo", nullable = false)
    private String regNo;       // 주민등록번호

//    @Column(name = "status", nullable = false)
//    @ColumnDefault("false")
//    private Boolean userStatus;




}
