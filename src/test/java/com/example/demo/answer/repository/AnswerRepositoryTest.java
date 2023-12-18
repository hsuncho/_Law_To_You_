package com.example.demo.answer.repository;

import com.example.demo.answer.entity.Answer;
import com.example.demo.consulting.repository.ConsultingRepository;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
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
class AnswerRepositoryTest {

    @Autowired
    ConsultingRepository consultingRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    LawyerRepository lawyerRepository;
    
    @Test
    @DisplayName("짧은 답변 등록")
    void registerShortAnswerTest() {
        //given
        Lawyer lawyer = lawyerRepository.findById("lll1234").orElseThrow();

        Answer answer = Answer.builder()
                .consulting(consultingRepository.findById(17).orElseThrow())
                .shortAns("17번 글에 대한 짧은 답변입니다.")
                .lawyer(lawyer)
                .writer(lawyer.getName())
                .build();

        //when
        Answer saved = answerRepository.save(answer);


        //then
        assertNotNull(saved);

        System.out.println("\n\n\n");
        System.out.println("saved = " + saved);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("상세 답변 등록")
    void registerDetailedAnswerTest() {
        //given
//        Lawyer lawyer = lawyerRepository.findById("lll1234").orElseThrow();

//        Answer.builder()
//                .answerNum(1)
//                .
//                .build();


        //when

        //then
    }

}