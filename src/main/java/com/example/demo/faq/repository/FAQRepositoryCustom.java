package com.example.demo.faq.repository;

import com.example.demo.faq.entity.FAQ;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface FAQRepositoryCustom {

    List<String> findByName(String largeSection);

    List<FAQ> findByLargeSec(String faq, Pageable pageable);

    List<FAQ> findByMiddleSec(String largeSection, String middleSection, Pageable pageable);

    List<FAQ> findByMiddleSecQna(String largeSection, String middleSection, int qno);
}
