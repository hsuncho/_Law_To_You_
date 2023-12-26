package com.example.demo.faq.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FAQMiddleAndQMSDTO {

    private int qno;
    private String middleSection;
    private String subject;
    private String question;
    private String answer;
    public FAQMiddleAndQMSDTO(int qno, String middleSection, String subject, String question, String answer) {

        this.qno = qno;
        this.middleSection = middleSection;
        this.subject = subject;
        this.question = question;
        this.answer = answer;
    }
}
