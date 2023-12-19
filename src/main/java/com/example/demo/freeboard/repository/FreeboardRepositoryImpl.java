package com.example.demo.freeboard.repository;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.member.user.entity.QUser;
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

    private BooleanExpression writerSelect(String content, Boolean flag) {
        return flag ? freeboard.writer.trim().contains(content.trim()) : null;
    }

    private BooleanExpression titleAndContent(String content, Boolean flag) {
        return !flag ? freeboard.content.trim().contains(content.trim())
                .or(freeboard.title.trim().contains(content.trim())) : null;
    }

}
