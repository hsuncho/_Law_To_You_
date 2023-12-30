package com.example.demo.freeboard.repository;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.member.Member;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.demo.freeboard.entity.QFreeboard.*;

@RequiredArgsConstructor
public class FreeboardRepositoryImpl implements FreeboardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 게시글 (제목+내용) 검색
    @Override
    public List<Freeboard> findByContent(String content, boolean flag) {
        return queryFactory
                .selectFrom(freeboard)
                .where(writerSelect(content, flag)
                        ,titleAndContent(content, flag))
                .fetch();
    }


    // 유저 로그인 여부 확인
    @Override
    public List<Freeboard> findAllByUser(User user) {
        return queryFactory
                .selectFrom(freeboard)
                .where(freeboard.user.id.eq(user.getId()))
                .fetch();
    }

    @Override
    public List<Freeboard> findAllByLawyer(Lawyer lawyer) {
        return queryFactory
                .selectFrom(freeboard)
                .where(freeboard.lawyer.lawyerId.eq(lawyer.getLawyerId()))
                .fetch();
    }

    @Override
    public boolean findByUserBoard(User user, int bno) {
        return queryFactory
                .select(freeboard.bno)
                .from(freeboard)
                .where(freeboard.bno.eq(bno)
                        .and(freeboard.user.id.eq(user.getId())))
                .fetchFirst() != null;
    }

    @Override
    public boolean findByLawyerBoard(Lawyer lawyer, int bno) {
        return queryFactory
                .select(freeboard.bno)
                .from(freeboard)
                .where(freeboard.bno.eq(bno)
                        .and(freeboard.lawyer.lawyerId.eq(lawyer.getLawyerId())))
                .fetchFirst() != null;
    }

    // 검색시 게시물 개수
    @Override
    public int findByContentCNT(String search, boolean flag) {
        return Math.toIntExact(queryFactory
                .select(freeboard.count())
                .from(freeboard)
                .where(writerSelect(search, flag)
                        ,titleAndContent(search, flag))
                .fetchFirst());
    }


    private BooleanExpression writerSelect(String content, Boolean flag) {
        return flag ? freeboard.writer.trim().contains(content.trim()) : null;
    }

    private BooleanExpression titleAndContent(String content, Boolean flag) {
        return !flag ? freeboard.content.trim().contains(content.trim())
                .or(freeboard.title.trim().contains(content.trim())) : null;
    }

}
