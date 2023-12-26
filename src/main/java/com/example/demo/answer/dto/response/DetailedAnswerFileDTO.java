package com.example.demo.answer.dto.response;

import com.example.demo.answer.entity.Answer;
import com.example.demo.answer.entity.AnswerFile;
import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DetailedAnswerFileDTO {
    private String route;

    public AnswerFile toEntity(Answer saved) {
        return AnswerFile.builder()
                .route(this.route)
                .answer(saved)
                .build();
    }

}
