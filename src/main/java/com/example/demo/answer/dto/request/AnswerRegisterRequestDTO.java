package com.example.demo.answer.dto.request;

import com.example.demo.answer.entity.Answer;
import com.example.demo.consulting.entity.Consulting;
import com.example.demo.member.lawyer.entity.Lawyer;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AnswerRegisterRequestDTO {

    @NotBlank
    private String shortAns;

    private int reqHammer;

    public Answer toEntity(Consulting consulting, Lawyer lawyer) {
        return Answer.builder()
                .writer(lawyer.getName())
                .shortAns(this.shortAns)
                .reqHammer(this.reqHammer)
                .consulting(consulting)
                .lawyer(lawyer)
                .build();

    }

}
