package com.example.demo.reply.dto.response;


import com.example.demo.reply.entity.Reply;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyDetailResponseDTO {

    private int rno;
    private int bno;
    private String lawyerId;
    private String userId;
    private String content;
    private String writer;
    @JsonFormat(pattern = "yyyy/MM/dd") // 제이슨으로 가져올 때 이 타입으로 매핑해서 줌 이 라이브러리 사용
    private LocalDateTime regDate;
    private boolean deleteButton;

    // 댓글 등록 , 댓글 리스트
    public ReplyDetailResponseDTO(Reply saved) {
        if (saved.getLawyer() != null) {
            this.lawyerId = saved.getLawyer().getLawyerId();
        }
        if (saved.getUser() != null) {
            this.userId = saved.getUser().getId();
        }
        this.rno = saved.getRno();
        this.bno = saved.getFreeboard().getBno();
        this.content = saved.getContent();
        this.writer = saved.getWriter();
        this.regDate = saved.getRegDate();
    }

}
