package com.example.demo.token.filter;

import com.example.demo.member.Member;
import com.example.demo.member.MemberRepository;
import com.example.demo.token.auth.TokenMemberInfo;
import com.example.demo.token.auth.TokenProvider;
import com.example.demo.token.dto.TokenDTO;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtMemberFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {

            String accessToken = parseBearerToken(request);
            String refreshToken = response.getHeader("Refresh-Token");
            log.info("\n\n\nJWT Token Filter 작동중 - accessToken: {}\nrefreshToken: {}\n\n\n", accessToken, refreshToken);

            // 토큰 위조 검사 및 인증 완료
            if (accessToken != null && !accessToken.equals("null")) {

                if(tokenProvider.validateAndGetTokenMemberInfo(accessToken)!=null) {
                    // accessToken 값이 유효하다면 security context에 인증 정보 저장

                    setAuthentication(request, accessToken);

                } else if (refreshToken != null&& !refreshToken.equals("null")) {
                    // accessToken 만료됨 + refreshToken 존재함
                    if(tokenProvider.validateRefreshToken(refreshToken) != null) {
                        // refreshToken으로 아이디 정보 가져오기
                        String loginId = tokenProvider.getIdFromToken(refreshToken);

                        // 새로운 accessToken 발급
                        String newAccessToken = tokenProvider.createToken(
                                memberRepository.findById(loginId).orElseThrow()
                        ).getAccessToken();
                        
                        // 헤더에 새로운 accessToken 추가
                        response.setHeader("Authority", newAccessToken);
                        setAuthentication(request, newAccessToken);

                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("서명이 일치하지 않습니다! 토큰이 위조되었습니다!");
        }

        filterChain.doFilter(request, response);

    }

    private void setAuthentication(HttpServletRequest request, String token) {

        TokenMemberInfo memberInfo = tokenProvider.validateAndGetTokenMemberInfo(token);

        // 인가 정보 리스트
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_" + memberInfo.getAuthority()));

        // 인증 완료 처리
        // 스프링 시큐리티에게 인증 정보 전달해서 인증 정보를 활용할 수 있도록 설정
        AbstractAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(
                memberInfo, // 컨트롤러에서 활용할 멤버 정보
                null, // 인증된 사용자 비밀번호
                authorityList // 인가 정보(권한 정보)
        );

        // 인증 완료 처리 시 클라이언트의 요청 정보 세팅
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 스프링 시큐리티 컨테이너에 인증 정보 객체 등록
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private String parseBearerToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        log.info("bearerToken: " + bearerToken);

        // 앞에 Bearer 제거하는 작업
        if(StringUtils.hasText(bearerToken)
                && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
