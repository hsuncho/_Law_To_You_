package com.example.demo.answer.dto.response;

import com.example.demo.freeboard.dto.response.PageResponseDTO;
import lombok.*;

import java.util.List;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AnswerListResponseDTO {

    private int count; // 총 답변 개수
    private List<AnswerDetailResponseDTO> answerList;

}
