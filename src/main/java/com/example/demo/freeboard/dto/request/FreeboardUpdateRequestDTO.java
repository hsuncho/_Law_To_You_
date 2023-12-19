package com.example.demo.freeboard.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Builder
public class FreeboardUpdateRequestDTO {


    private int bno;
    private String userId;
    private String title;
    private String content;


    public FreeboardUpdateRequestDTO(int bno,String userId, String title, String content) {
        this.bno = bno;
        this.userId = userId;
        this.title = title;
        this.content = content;
    }
}
