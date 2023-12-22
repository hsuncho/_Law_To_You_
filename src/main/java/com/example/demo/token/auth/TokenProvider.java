package com.example.demo.token.auth;

import com.example.demo.member.Member;
import com.example.demo.member.MemberRepository;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.token.dto.TokenDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TokenProvider {

    private UserRepository userRepository;

    private LawyerRepository lawyerRepository;



    private static final String ACCESS_TOKEN = "Authorization";
    private static final String REFRESH_TOKEN = "Refresh_Token";

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // access token 만료 기한
    private final Date expiry = Date.from(
//            Instant.now().plus(6, ChronoUnit.HOURS)
            Instant.now().plus(2, ChronoUnit.MINUTES)
    );

    // refresh token 만료 기한
    private final Date expiryForRefresh = Date.from(
//            Instant.now().plus(2, ChronoUnit.WEEKS)
            Instant.now().plus(10, ChronoUnit.MINUTES)
    );
    
    // TokenDTO를 생성하는 메서드
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

    // 토큰 검증
    public Jws<Claims> validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token);

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    // 토큰 검증 및 TokenMemberInfo 가져오기
    public TokenMemberInfo validateAndGetTokenMemberInfo(String token) {
        Claims claims = validateToken(token).getBody();

        log.info("claims: {}", claims);

        return TokenMemberInfo.builder()
                .id(claims.getSubject())
                .authority(claims.get("authority", String.class))
                .build();
    }
    
    // refresh token 검증
    // DB에 저장되어 있는 token과 비교
    public String validateRefreshToken(String token) {
        
        try {

            // 1차 토큰 검증
            if(validateToken(token) == null) return null;

            Claims claims = validateToken(token).getBody();
            String id = claims.getSubject();

            // DB에 저장한 토큰과 비교
            String refreshToken = null;
            if(claims.get("authority").equals("user")) {
                refreshToken = userRepository.findById(id).orElseThrow().getRefreshToken();
            } else if(claims.get("authority").equals("lawyer")) {
                refreshToken = lawyerRepository.findById(id).orElseThrow().getRefreshToken();
            }

            if(!token.equals(refreshToken)) {
                return null;
            }

            // refresh 토큰의 만료 기한이 지나지 않았을 경우, 새로운 access 토큰 생성 후 return
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

    public String getIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

}
