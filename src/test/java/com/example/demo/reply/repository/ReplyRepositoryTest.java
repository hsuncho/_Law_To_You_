package com.example.demo.reply.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.freeboard.repository.FreeboardRepository;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.lawyer.repository.LawyerRepository;
import com.example.demo.member.user.entity.User;
import com.example.demo.member.user.repository.UserRepository;
import com.example.demo.reply.entity.Reply;

@SpringBootTest
@Transactional
@Rollback(false)
class ReplyRepositoryTest {

    @Autowired
    ReplyRepository replyRepository;

    @Autowired
    FreeboardRepository freeboardRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LawyerRepository lawyerRepository;

    @Test
    @DisplayName("댓글 등록Test")
    void replyTest() {
        //given

        User user = userRepository.findById("park1234").orElseThrow();
        Lawyer lawyer = lawyerRepository.findById("lll1234").orElseThrow();

        Reply r1 = Reply.builder()
                .content("아아아아아아아아아")
                .writer("김똥꾸")
                .user(user)
                .lawyer(lawyer)
                .build();

        //when
        Reply saved = replyRepository.save(r1);
        //then
        System.out.println("saved = " + saved);
        assertNotNull(saved);

    }
    
    @Test
    @DisplayName("댓글 삭제Test")
    void replyDeleteTest() {
        //given
        int rno = 2;
        //when
        replyRepository.deleteById(rno);

        //then
    }

    // 댓글 삭제(버튼 띄우기)


    @Test
    @DisplayName("댓글 목록")
    void replyListTest() {
        //given
        List<Reply> replyList = replyRepository.findAll();
        //when

        replyList.forEach(System.out::println);
        //then
        assertEquals(3, replyList.size());

    }
    

}

