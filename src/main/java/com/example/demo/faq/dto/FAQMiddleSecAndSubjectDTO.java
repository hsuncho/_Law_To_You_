package com.example.demo.faq.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FAQMiddleSecAndSubjectDTO {

    private int rowNum;
    private String middleSection;
    private String subject;

    public FAQMiddleSecAndSubjectDTO(int rowNum, String middleSection, String subject) {
        this.rowNum = rowNum;
        this.middleSection = middleSection;
        this.subject = subject;
    }
}
