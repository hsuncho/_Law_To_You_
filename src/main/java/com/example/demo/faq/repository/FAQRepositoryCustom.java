package com.example.demo.faq.repository;

import com.example.demo.faq.entity.FAQ;

import java.util.List;

public interface FAQRepositoryCustom {

    List<String> findByName(String largeSection);

    List<FAQ> findByLargeSec(String faq);

    List<FAQ> findByMiddleSec(String largeSection, String middleSection);

    List<FAQ> findByMiddleSecQna(String largeSection, String middleSection, int qno);
}
