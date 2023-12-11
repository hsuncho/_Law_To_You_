package com.example.demo.reply.repository;

import com.example.demo.freeboard.repository.FreeboardRepository;
import com.example.demo.reply.entity.Reply;
import org.hibernate.boot.TempTableDdlTransactionHandling;
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
class ReplyRepositoryTest {

    @Autowired
    ReplyRepository replyRepository;

    @Autowired
    FreeboardRepository freeboardRepository;

    @Test
    @DisplayName("댓글 등록Test")
    void replyTest() {
        //given
        Reply r1 = Reply.builder()
                .rno(1)
                .content("아 이거 그렇게 하는거 아닌데")
                .writer("김춘식")
                .build();

        //when
        Reply saved = replyRepository.save(r1);
        //then
        System.out.println("saved = " + saved);
        assertNotNull(saved);

    }

}

