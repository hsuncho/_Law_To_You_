package com.example.demo.reply.repository;

import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.user.entity.User;
import com.example.demo.reply.entity.Reply;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReplyRepositoryCustom{

    // 해당 게시물에 댓글 리스트
    List<Reply> findByBno(int bno, Pageable pageable);

    boolean findByUserReply(String writerId, int rno);
}
