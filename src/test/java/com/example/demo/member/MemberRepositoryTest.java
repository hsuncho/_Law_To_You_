package com.example.demo.member;

import com.example.demo.member.user.entity.User;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.token.auth.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("insert test")
    void insertTest() {
        //given
        User user = userRepository.findById("park1234").orElseThrow();

        Member memberUser = Member.builder()
                .id(user.getId())
                .authority((user.getAuthority()))
                .user(user)
                .build();
        //when
        memberRepository.save(memberUser);

        //then
        System.out.println("\n\n\n");
        System.out.println("memberUser = " + memberUser);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("로그인을 한 사용자에게는 토큰을 발급할 예정")
    void createTokenTest() {
        //given
        Member foundMember = memberRepository.findById("park1234").orElseThrow();

        User foundUser = foundMember.getUser();

        String token = tokenProvider.createToken(foundMember).getAccessToken();

        //when
        User loginUser = User.builder()
                .id(foundUser.getId())
                .password(foundUser.getPassword())
                .nickname(foundUser.getNickname())
                .email(foundUser.getEmail())
                .authority(foundMember.getAuthority())
                .joinMethod(foundUser.getJoinMethod())
                .accessToken(token)
                .build();

        User saved = userRepository.save(loginUser);

        //then
        assertNotNull(saved.getAccessToken());

        System.out.println("\n\n\n");
        System.out.println("saved.getAccessToken() = " + saved.getAccessToken());
        System.out.println("\n\n\n");

    }



}