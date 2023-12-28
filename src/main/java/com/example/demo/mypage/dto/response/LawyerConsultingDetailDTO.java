package com.example.demo.mypage.dto.response;

import com.example.demo.answer.entity.Answer;
import com.example.demo.consulting.entity.Consulting;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;


@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LawyerConsultingDetailDTO {

    private int consultNum;

    private String title;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime regDate;

    private int adopt;

    private boolean ifDetailedPresent;

    public LawyerConsultingDetailDTO(Answer answer) {

        Consulting consulting = answer.getConsulting();

        this.consultNum = consulting.getConsultNum();
        this.title = consulting.getTitle();
        this.regDate = consulting.getRegDate();
        this.adopt = answer.getAdopt();

        if(consulting.getUpdateDate ()!= null) {
            this.ifDetailedPresent = true;
        } else {
            this.ifDetailedPresent = false;
        }
    }

}


