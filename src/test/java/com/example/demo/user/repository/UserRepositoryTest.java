package com.example.demo.user.repository;

import com.example.demo.member.Member;
import com.example.demo.member.MemberRepository;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.entity.User;
import com.example.demo.token.auth.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LawyerRepository lawyerRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("join test(사용자)")
    void saveTest() {
        //given
        User newUser = User.builder()
                .id("ppp1234")
                .password("aaa1111!")
                .nickname("야옹이")
                .email("ppp1234@naver.com")
                .joinMethod("web")
                .build();

        //when
        User savedUser = userRepository.save(newUser);

        //then
        assertNotNull(savedUser);
    }

    
    @Test
    @DisplayName("아이디 중복 체크를 하면 중복값이 true여야 한다.")
    void idIsPresent() {
        //given
        String id = "lll1234";

        //when
        boolean flag = userRepository.existsById(id) || lawyerRepository.existsById(id);

        //then
        assertTrue(flag);
    }
    
    @Test
    @DisplayName("닉네임 중복 체크를 하면 중복값이 true여야 한다.")
    void nicknameIsPresent() {
        //given
        String nickname = "박영희";

        //when
        boolean flag = userRepository.existsByNickname(nickname);

        //then
        assertTrue(flag);
    }

    @Test
    @DisplayName("이메일 중복 체크를 하면 중복값이 true여야 한다.")
    void emailIsPresent() {
        //given
        String email = "park1234@google.com";

        //when
        boolean flag = userRepository.existsByEmail(email) || lawyerRepository.existsByEmail(email);

        //then
        assertTrue(flag);
    }
  
    @Test
    @DisplayName("array")
    void array() {
        //given

        //when
        List<String> list1 = new ArrayList<>();
        list1.add("안녕");
        list1.add("클레오파트라");

        List<String> list2 = new ArrayList<>();
        list2.addAll(list1);

        //then
        System.out.println("\n\n\n");
        System.out.println("list2 = " + list2);
        System.out.println("\n\n\n");

    }

    
}