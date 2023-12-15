package com.example.demo.member.user.repository;

import com.example.demo.member.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    // db에 refreshToken이 존재하는가
    boolean existsByRefreshToken(String refreshToken);
}
