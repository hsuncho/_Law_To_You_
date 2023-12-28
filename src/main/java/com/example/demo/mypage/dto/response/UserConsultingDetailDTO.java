package com.example.demo.mypage.dto.response;

import com.example.demo.answer.entity.Answer;
import com.example.demo.consulting.entity.Consulting;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@EqualsAndHashCode @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserConsultingDetailDTO {

    private int consultNum;

    private String title;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime regDate;

    private boolean isAnswered;

    public UserConsultingDetailDTO(Consulting consulting) {

        List<Answer> answerList = consulting.getAnswerList();

        this.consultNum = consulting.getConsultNum();
        this.title = consulting.getTitle();
        this.regDate = consulting.getRegDate();

        if(!answerList.isEmpty()) {
            this.isAnswered = true;
        } else {
            this.isAnswered = false;
        }


    }



}
