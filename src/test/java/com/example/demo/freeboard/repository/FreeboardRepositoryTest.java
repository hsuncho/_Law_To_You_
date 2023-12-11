package com.example.demo.freeboard.repository;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.repository.FreeboardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class FreeboardRepositoryTest {

    @Autowired
    FreeboardRepository freeboardRepository;

    @Test
    @DisplayName("게시판 글 작성 테스트")
    void writeTest() {
        //given
        Freeboard freeboard = Freeboard.builder()
                .writer("김스프링")
                .title("춘식이는 왜 춘식이일까??")
                .content("그것은 춘식이기때문")
                .build();
        //when
        Freeboard saved = freeboardRepository.save(freeboard);
        //then
        assertNotNull(saved);

    }
    
    @Test
    @DisplayName("게시판 글 수정 테스트")
    void updateTest() {
        //given
        int bno = 1;
        String newContent = "춘식이야 수정됐다.";
        //when
        Optional<Freeboard> freeboard = freeboardRepository.findById(bno);
        System.out.println("freeboard = " + freeboard);
        freeboard.ifPresent(f -> {
            f.setContent(newContent);

            freeboardRepository.save(f);
        });
        //then
        assertTrue(freeboard.isPresent());

        Freeboard f = freeboard.get();
        assertEquals("춘식이야 수정됐다.", f.getContent());

    }

    @Test
    @DisplayName("게시글 전체 조회")
    void findAllTest() {
        //given

        //when
        List<Freeboard> freeboard = freeboardRepository.findAll();


        //then
        freeboard.forEach(System.out::println);

        assertEquals(2, freeboard.size());

    }
    @Test
    @DisplayName("게시글 검색(QueryDSL사용)")
    void searchTest() {
        //given
        String content = "춘식이";
        //when
        List<Freeboard> result = freeboardRepository.findByContent(content);
        //then
        System.out.println(result);
        assertEquals(2, result.size());
        assertEquals("김춘식", result.get(0).getWriter());

    }


    @Test
    @DisplayName("게시판 상세 글 불러오기")
    void detailTest() {
        int bno = 1;
        //given
        Freeboard result = freeboardRepository.findById(bno).orElseThrow();
        //when

        //then
        System.out.println("글 불러오기:" + result);
    }


}