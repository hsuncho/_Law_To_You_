package com.example.demo.answer.dto.response;

import com.example.demo.answer.entity.Answer;
import com.example.demo.answer.entity.AnswerFile;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@EqualsAndHashCode @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DetailedResponseDTO {

    private String writer;
    private String detailedAns;
    private List<String> routes;

    public DetailedResponseDTO(Answer saved) {

        this.detailedAns = saved.getDetailAns();
        this.writer = saved.getWriter();

        this.routes = saved.getAnswerFiles()
                .stream()
                .map(AnswerFile::getRoute)
                .collect(Collectors.toList());
    }

}
