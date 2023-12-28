package com.example.demo.consulting.dto.response;

import com.example.demo.consulting.entity.Consulting;
import com.example.demo.consulting.entity.ConsultingFile;
import com.example.demo.consulting.entity.DetailedConsultingFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Setter @Getter
@EqualsAndHashCode @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ConsultingDetailResponseDTO {

    private int consultNum;
    private String title;
    private String writer;
    private String content;
    private List<String> routes;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime regDate;


    public ConsultingDetailResponseDTO(Consulting saved) {

            this.consultNum = saved.getConsultNum();
            this.regDate = saved.getRegDate();
            this.title = saved.getTitle();
            this.writer = saved.getWriter();
            this.content = saved.getContent();

            this.routes = saved.getConsultingFiles()
                    .stream()
                    .map(ConsultingFile::getRoute)
                    .collect(Collectors.toList());

    }

}
