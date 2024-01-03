package com.example.demo.mypage.dto.response;

import com.example.demo.freeboard.entity.Freeboard;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MyPageFreeboardDetailDTO {

    private int bno;
    private String title;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime date;

    public MyPageFreeboardDetailDTO(Freeboard freeboard) {
        this.bno = freeboard.getBno();
        this.title = freeboard.getTitle();
        this.date = freeboard.getRegDate();

    }

}
