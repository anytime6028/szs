package com.project.szs.Auth.Repository;

import com.project.szs.Auth.Entity.AmountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmountInfoRepository extends JpaRepository<AmountInfo, Long> {

    AmountInfo findAmountInfoByMemberId(Long memberId);
    Optional<AmountInfo> getAmountInfoByMemberId(Long memeberId);
}
