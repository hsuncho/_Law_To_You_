package com.example.demo.faq.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FAQMiddleAndQMSDTO {

    private int rowNum;
    private int qno;
    private String middleSection;
    private String subject;


    public FAQMiddleAndQMSDTO(int rowNum,int qno, String middleSection, String subject) {

        this.rowNum = rowNum;
        this.qno = qno;
        this.middleSection = middleSection;
        this.subject = subject;
    }
}
