package com.example.demo.token.repository;

import com.example.demo.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findById(String id);
}
