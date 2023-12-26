package com.example.demo.reply.dto.request;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.user.entity.User;
import com.example.demo.reply.entity.Reply;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyCreateRequestDTO {

    @NotNull
    private int bno;

    @NotBlank
    private String content;

    // 일반 사용자 댓글 등록
    public Reply toEntity(User user, Freeboard freeboard) {
        return Reply.builder()
                .writer(user.getNickname())
                .content(this.content)
                .user(user)
                .freeboard(freeboard)
                .build();
    }

    // 변호사 댓글 등록
    public Reply toEntity(Lawyer lawyer, Freeboard freeboard) {
        return Reply.builder()
                .writer(lawyer.getName())
                .content(this.content)
                .lawyer(lawyer)
                .freeboard(freeboard)
                .build();
    }


}
