package com.example.demo.faq.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class FAQListCountDTO {

    private int Count;

    private List<FAQMiddleSecAndSubjectDTO> faqMiddleSecAndSubjectDTOList = new ArrayList<>();


    public FAQListCountDTO(int count, List<FAQMiddleSecAndSubjectDTO> faqMiddleSecAndSubjectDTOList) {

        this.Count = count;
        this.faqMiddleSecAndSubjectDTOList.addAll(faqMiddleSecAndSubjectDTOList);

    }

}

