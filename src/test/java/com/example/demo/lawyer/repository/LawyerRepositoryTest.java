package com.example.demo.lawyer.repository;

import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class LawyerRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LawyerRepository lawyerRepository;

    @Test
    @DisplayName("join test(변호사)")
    void saveTest() {
        //given
        Lawyer lawyer = Lawyer.builder()
                .email("lll1234@google.com")
                .lawyerId("lll1234")
                .lawyerPw("aaa1111!")
                .name("홍길동")
                .lawyerNum(123456)
                .authority("lawyer")
                .build();

        //when
        Lawyer saved = lawyerRepository.save(lawyer);

        //then
        assertNotNull(saved);
    }

    @Test
    @DisplayName("아이디 중복 체크를 하면 중복값이 true여야 한다.")
    void idIsPresent() {
        //given
        String id = "lll1234";

        //when
        // user에서 중복되거나 lawyer에서 중복된다면 true, 둘 다 중복되지 않을 경우에만 false
        boolean flag = lawyerRepository.existsById(id) ||  userRepository.existsById(id);

        //then
        assertTrue(flag);
    }

    @Test
    @DisplayName("이메일 중복 체크를 하면 중복값이 true여야 한다.")
    void emailIsPresent() {
        //given
        String email = "lll1234@google.com";

        //when
        boolean flag = lawyerRepository.existsByEmail(email) || userRepository.existsByEmail(email);

        //then
        assertTrue(flag);
    }

}