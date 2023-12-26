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

<<<<<<< HEAD
    private int largeCount;

    private List<String> middleSection = new ArrayList<>();

=======
    @Builder.Default
    private List<String> middleSection = new ArrayList<>();


    @Builder.Default
>>>>>>> 358c9e4e6c03ceb0d7af698e525e10bead74336b
    private List<FAQMiddleAndQMSDTO> listSearchedByLargeSec = new ArrayList<>();

    public FAQMiddleSecAndList(int count, List<String> middleSec, List<FAQMiddleAndQMSDTO> largeSecList) {
        this.largeCount = count;
        this.middleSection = middleSec;
        this.listSearchedByLargeSec = largeSecList;
    }

}
