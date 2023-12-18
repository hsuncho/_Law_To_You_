package com.example.demo.faq.dto;

import com.example.demo.faq.entity.FAQ;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQDetailResponseDTO {

    private int qno;
    private String answer;
    private String largeSection;
    private int largeSectionNum;
    private String middleSection;
    private String question;
    private int middleSectionNum;
    private String subject;

    public FAQDetailResponseDTO(FAQ faq) {
        this.qno = faq.getQno();
        this.answer = faq.getAnswer();
        this.largeSection = faq.getLargeSection();
        this.largeSectionNum = faq.getLargeSectionNum();
        this.middleSection = faq.getMiddleSection();
        this.question = faq.getQuestion();
        this.middleSectionNum = faq.getMiddleSectionNum();
        this.subject = faq.getSubject();

    }

}
