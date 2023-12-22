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
    private String question;
    private String answer;

    public FAQMiddleSecAndSubjectDTO(int rowNum, String middleSection, String subject, String question, String answer) {
        this.rowNum = rowNum;
        this.middleSection = middleSection;
        this.subject = subject;
        this.question = question;
        this.answer = answer;
    }
}
