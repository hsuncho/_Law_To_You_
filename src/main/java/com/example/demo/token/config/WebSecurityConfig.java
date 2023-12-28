package com.example.demo.token.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import com.example.demo.token.filter.JwtMemberFilter;

import lombok.RequiredArgsConstructor;

//@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final JwtMemberFilter jwtMemberFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Security 모듈이 기본적으로 제공하는 보안 정책 해제.
        http
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/user/logout", "/api/counsel/**").authenticated()
                .antMatchers("/", "/api/user/**", "/api/lawyer/**","/api/counsel/content/**", "/api/answer", "/api/faq/**").permitAll()
                .anyRequest().authenticated();

        // 토큰 인증 필터 연결
        // jwtAuthFilter부터 연결 -> CORS 필터 이후에 통과하도록 설정.
        http.addFilterAfter(
                jwtMemberFilter,
                CorsFilter.class
        );
        return http.build();
    }

}
