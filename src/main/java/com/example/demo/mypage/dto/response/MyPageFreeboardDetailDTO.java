package com.example.demo.mypage.dto.response;

import com.example.demo.freeboard.entity.Freeboard;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MyPageFreeboardDetailDTO {

    private int bno;
    private String title;
    private LocalDateTime date;

    public MyPageFreeboardDetailDTO(Freeboard freeboard) {
        this.bno = freeboard.getBno();
        this.title = freeboard.getTitle();
        this.date = freeboard.getRegDate();

    }

}
