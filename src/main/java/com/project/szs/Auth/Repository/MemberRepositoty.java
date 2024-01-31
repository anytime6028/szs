package com.project.szs.Auth.Repository;

import com.project.szs.Auth.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepositoty extends JpaRepository<Member, Long> {
    Optional<Member> findUserByUserId(String userId);
    Optional<Member> findUserByName(String name);

    Member findMemberByUserId(String userId);


}
