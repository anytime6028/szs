package com.project.szs.Auth.Entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "amountInfo")
@Getter
@Setter
@Builder
@Entity
public class AmountInfo {

    @Id
    @Column(name = "memberId", nullable = false)
    private Long memberId;

    //총급여
    @Column(name = "totalPayment", nullable = false)
    private int totalPayment;

    //산출세액
    @Column(name = "taxAmount", nullable = false)
    private double taxAmount;

    //소득공제-보험료
    @Column(name = "insurance", nullable = false)
    private int insurance;

    //소득공제-교육비
    @Column(name = "education", nullable = false)
    private int education;

    //소득공제-기부금
    @Column(name = "donate", nullable = false)
    private int donate;

    //소득공제-의료비
    @Column(name = "medical", nullable = false)
    private int medical;

    //소득공제-퇴직연금
    @Column(name = "retirementPension", nullable = false)
    private double retirementPension;
}
