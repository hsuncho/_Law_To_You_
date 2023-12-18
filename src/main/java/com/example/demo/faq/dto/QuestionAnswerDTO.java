package com.example.demo.faq.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionAnswerDTO {

    private String question;
    private String answer;

    public QuestionAnswerDTO(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

}

