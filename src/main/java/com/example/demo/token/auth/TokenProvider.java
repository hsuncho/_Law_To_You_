package com.example.demo.token.auth;

import com.example.demo.member.Member;
import com.example.demo.token.dto.TokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TokenProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;


    public TokenDTO createToken(Member member) {

        Date expiry = Date.from(
                Instant.now().plus(6, ChronoUnit.HOURS)
        );

        LocalDate expiryDate = LocalDate.now().plusMonths(2);
        Date expiryForRefresh = Date.from(expiryDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // 추가 클레임
        Map<String, String> claims = new HashMap<>();
        claims.put("authority", member.getAuthority());

        String accessToken = Jwts.builder()
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes()),
                        SignatureAlgorithm.HS512
                )
                .setClaims(claims)
                .setIssuer("운영자") // iss: 발급자 정보
                .setIssuedAt(new Date()) // iat: 발급 시간
                .setExpiration(expiry) //exp: 만료시간
                .setSubject(member.getId()) // sub: 토큰을 식별할 수 있는 주요 데이터
                .compact();

        String refreshToken = Jwts.builder()
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes()),
                        SignatureAlgorithm.HS512
                )
                .setClaims(claims)
                .setIssuer("운영자") // iss: 발급자 정보
                .setIssuedAt(new Date()) // iat: 발급 시간
                .setExpiration(expiryForRefresh) //exp: 만료시간
                .setSubject(member.getId()) // sub: 토큰을 식별할 수 있는 주요 데이터
                .compact();

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenMemberInfo validateAndGetTokenMemberInfo(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        log.info("claims: {}", claims);

        return TokenMemberInfo.builder()
                .id(claims.getSubject())
                .authority(claims.get("authority", String.class))
                .build();
    }


}
