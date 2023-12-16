package com.example.demo.token.filter;

import com.example.demo.token.auth.TokenMemberInfo;
import com.example.demo.token.auth.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = parseBearerToken(request);
            log.info("JWT Token Filter 작동중 - token: {}", token);

            // 토큰 위조 검사 및 인증 완료
            if (token != null && !token.equals("null")) {
                TokenMemberInfo memberInfo = tokenProvider.validateAndGetTokenMemberInfo(token);

                // 인가 정보 리스트
                // 권한 체크에 사용할 필드 authority 추가함
                List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
                authorityList.add(new SimpleGrantedAuthority("ROLE_" + memberInfo.getAuthority().toString()));

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

        } catch (Exception e) {
            e.printStackTrace();
            log.info("서명이 일치하지 않습니다! 토큰이 위조되었습니다!");
        }

        filterChain.doFilter(request, response);

    }

    private String parseBearerToken(HttpServletRequest request) {

        // 요청 헤더에서 토큰 꺼내오기
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
