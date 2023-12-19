package com.example.demo.faq.repository;

import com.example.demo.faq.entity.FAQ;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.demo.faq.entity.QFAQ.fAQ;

@RequiredArgsConstructor
public class FAQRepositoryImpl implements FAQRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override // 대분류에 중분류 리스트 조회
    public List<String> findByName(String largeSection) {
        return  queryFactory
                .select(fAQ.middleSection)
                .from(fAQ)
                .where(fAQ.largeSection.trim().eq(largeSection))
                .distinct()
                .fetch();
    }

    @Override // 중분류에 번호, 중분류, 제목 조회
    public List<FAQ> findByLargeSec(String faq) {

        return queryFactory
                .select(Projections.fields(FAQ.class,fAQ.qno, fAQ.middleSection, fAQ.subject))
                .from(fAQ)
                .where(fAQ.largeSection.trim().eq(faq))
                .fetch();
    }

    @Override
    public List<FAQ> findByMiddleSec(String largeSection, String middleSection) {
        return queryFactory
                .select(Projections.fields(FAQ.class,fAQ.qno, fAQ.middleSection, fAQ.subject))
                .from(fAQ)
                .where(fAQ.largeSection.trim().eq(largeSection)
                        .and(fAQ.middleSection.eq(middleSection)))
                .fetch();
    }

    @Override
    public List<FAQ> findByMiddleSecQna(String largeSection, String middleSection, int qno) {

        return queryFactory
                .select(Projections.fields(FAQ.class, fAQ.question, fAQ.answer))
                .from(fAQ)
                .where(fAQ.middleSection.trim().eq(middleSection)
                        .and(fAQ.qno.eq(qno)))
                .fetch();
    }




}
