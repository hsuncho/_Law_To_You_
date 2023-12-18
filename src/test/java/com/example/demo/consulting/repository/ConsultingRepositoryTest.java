package com.example.demo.consulting.repository;

import com.example.demo.consulting.entity.Consulting;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class ConsultingRepositoryTest {

    @Autowired
    ConsultingRepository consultingRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    LawyerRepository lawyerRepository;

    @Test
    @DisplayName("온라인 상담 글 등록")
    void registerTest() {
        //given
        List<Consulting> consultingList = new ArrayList<>();

        for(int i = 1 ; i < 10 ; i++) {
            Consulting consulting = Consulting.builder()
                    .title("온라인 상담 제목 " + i)
                    .content("내용 " + i)
                    .largeSection("large")
                    .writer(userRepository.findById("park1234").orElseThrow().getNickname())
                    .build();

            //when
            Consulting saved = consultingRepository.save(consulting);

            consultingList.add(saved);

        }

        //then
        assertEquals(consultingList.size(), 9);

        System.out.println("\n\n\n");
        consultingList.forEach(System.out::println);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("등록 테스트 조인")
    void registerJoinTest() {
        //given
        Consulting consulting = Consulting.builder()
                .title("하하2")
                .content("호호2 ")
                .largeSection("large")
                .writer(userRepository.findAll().get(0).getNickname())
                .user(userRepository.findAll().get(0))
                .build();

        //when
        Consulting saved = consultingRepository.save(consulting);

        //then
        assertNotNull(saved);
        assertEquals(saved.getConsultNum(), 21);

        System.out.println("\n\n\n");
        System.out.println("saved = " + saved);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("온라인 상담 글 목록 테스트")
    void getListTest() {
        //given

        //when
        List<Consulting> consultingList = consultingRepository.findAll();

        //then
        assertEquals(consultingList.size(), 19);
        System.out.println("\n\n\n");
        consultingList.forEach(System.out::println);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("온라인 상담 글 상세보기")
    void getDetailedContentTest() {
        //given
        int consultNum = 11;

        //when
        Consulting consulting = consultingRepository.findById(consultNum).orElseThrow();

        //then
        assertEquals(consulting.getWriter(), "박영희");
        System.out.println("\n\n\n");
        System.out.println("consulting = " + consulting);
        System.out.println("\n\n\n");
    }
    
    @Test
    @DisplayName("깊은 상담 등록 테스트")
    void registerDetailedConsultingTest() {
        //given
        Consulting consulting = consultingRepository.findById(21).orElseThrow();

        Consulting update = Consulting.builder()
                .consultNum(21)
                .content(consulting.getContent())
                .title(consulting.getTitle())
                .writer(consulting.getWriter())
                .largeSection(consulting.getLargeSection())
                .regDate(consulting.getRegDate())
                .updateTitle("수정된 제목")
                .updateContent("수정된 내용")
                .updateDate(LocalDateTime.now())
                .build();

        //when
        Consulting saved = consultingRepository.save(update);

        //then
        assertNotNull(saved);
        assertEquals(saved.getTitle(), update.getTitle());
    }

}