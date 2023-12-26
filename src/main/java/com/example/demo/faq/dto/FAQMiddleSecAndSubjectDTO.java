package com.example.demo.faq.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class FAQMiddleSecAndSubjectDTO {

    private String middleSection;
    private String subject;
    private String question;
    private String answer;

    public FAQMiddleSecAndSubjectDTO(String middleSection, String subject, String question, String answer) {
        this.middleSection = middleSection;
        this.subject = subject;
        this.question = question;
        this.answer = answer;
    }
}
