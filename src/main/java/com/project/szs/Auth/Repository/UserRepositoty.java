package com.project.szs.Auth.Repository;

import com.project.szs.Auth.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoty extends JpaRepository<User, Long> {
    Optional<User> findUserByUserId(String userId);
}
