package com.example.demo.freeboard.repository;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.entity.QFreeboard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.demo.freeboard.entity.QFreeboard.*;

@RequiredArgsConstructor
public class FreeboardRepositoryImpl implements FreeboardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Freeboard> findByWriter(String writer) {
        return queryFactory
                .selectFrom(freeboard)
                .where(freeboard.writer.eq(writer))
                .fetch();
    }

    @Override
    public List<Freeboard> findByContent(String content) {
        return queryFactory
                .selectFrom(freeboard)
                .where(freeboard.content.contains(content))
                .fetch();
    }
}
