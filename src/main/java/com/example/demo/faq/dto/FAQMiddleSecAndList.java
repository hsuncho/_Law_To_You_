package com.example.demo.faq.dto;

import com.example.demo.faq.entity.FAQ;
import com.querydsl.core.Tuple;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@Builder
@Slf4j
public class FAQMiddleSecAndList {

    private int largeCount;

    @Builder.Default
    private List<String> middleSection = new ArrayList<>();

    @Builder.Default
    private List<FAQMiddleAndQMSDTO> listSearchedByLargeSec = new ArrayList<>();

    public FAQMiddleSecAndList(int count, List<String> middleSec, List<FAQMiddleAndQMSDTO> largeSecList) {
        this.largeCount = count;
        this.middleSection = middleSec;
        this.listSearchedByLargeSec = largeSecList;
    }

}
