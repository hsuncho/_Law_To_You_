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
@NoArgsConstructor
@Builder
@Slf4j
public class FAQMiddleSecAndList {

    @Builder.Default
    private List<String> middleSection = new ArrayList<>();


    @Builder.Default
    private List<FAQMiddleAndQMSDTO> listSearchedByLargeSec = new ArrayList<>();


    public FAQMiddleSecAndList(List<String> middleList, List<FAQMiddleAndQMSDTO> list) {

        // 리스트 변경
        this.middleSection.addAll(middleList);

        this.listSearchedByLargeSec.addAll(list);


    }


}
