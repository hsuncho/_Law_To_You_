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

    // access token 만료 기한
    private final Date expiry = Date.from(
            Instant.now().plus(6, ChronoUnit.HOURS)
    );

    // refresh token 만료 기한
    private final Date expiryForRefresh = Date.from(
            Instant.now().plus(2, ChronoUnit.WEEKS)
    );

    public TokenDTO createToken(Member member) {

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

    public String validateRefreshToken(String refreshToken) {
        
        try {

            Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            log.info("claims: {}", claims);

            // refresh 토큰의 만료 기한이 지나지 않았을 경우, 새로운 access 토큰을 생성
            if(!claims.getExpiration().before(new Date())) {
                return recreationAccessToken(claims.get("sub").toString(), claims.get("authority"));
            }
            
        } catch (Exception e) {
            // refresh token 만료된 경우 로그인 필요
            return null;
        }
        
        return null;

    }

    public String recreationAccessToken(String id, Object authority) {

        Claims claims = Jwts.claims().setSubject(id);
        claims.put("authority", authority);

        // Access Token
        return Jwts.builder()
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes()),
                        SignatureAlgorithm.HS512
                )
                .setClaims(claims)
                .setIssuer("운영자") // iss: 발급자 정보
                .setIssuedAt(new Date()) // iat: 발급 시간
                .setExpiration(expiry) //exp: 만료시간
                .compact();
    }


}
