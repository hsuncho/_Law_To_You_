package com.example.demo.reply.repository;

import com.example.demo.freeboard.entity.QFreeboard;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.user.entity.User;
import com.example.demo.reply.entity.QReply;
import com.example.demo.reply.entity.Reply;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Select;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.demo.freeboard.entity.QFreeboard.*;
import static com.example.demo.reply.entity.QReply.*;

@RequiredArgsConstructor
public class ReplyRepositoryImpl implements ReplyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 해당 게시물에 댓글 리스트
    @Override
    public List<Reply> findByBno(int bno, Pageable pageable) {
        return queryFactory
                .selectFrom(reply)
                .leftJoin(reply.user)
                .fetchJoin()
                .leftJoin(reply.lawyer)
                .fetchJoin()
                .where(reply.freeboard.bno.eq(bno))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public boolean findByUserReply(String writerId,  int rno) {
        return queryFactory
                .select(reply.rno)
                .from(reply)
                .where(reply.rno.eq(rno)
                        .and(reply.user.id.eq(writerId)
                                .or(reply.lawyer.lawyerId.eq(writerId))))
                .fetchFirst() != null;
    }


}
