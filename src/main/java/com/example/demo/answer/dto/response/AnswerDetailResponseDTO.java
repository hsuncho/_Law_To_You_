package com.example.demo.answer.dto.response;

import com.example.demo.answer.entity.Answer;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AnswerDetailResponseDTO {

    private int answerNum;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime regDate;

    private String writer;

    private String shortAns;

    private int reqHammer;

    public AnswerDetailResponseDTO(Answer saved) {

        this.answerNum = saved.getAnswerNum();
        this.regDate = saved.getRegDate();
        this.writer = saved.getWriter();
        this.shortAns = saved.getShortAns();
        this.reqHammer = saved.getReqHammer();
    }



}
