package com.example.demo.consulting.dto.response;

import com.example.demo.consulting.entity.Consulting;
import com.example.demo.consulting.entity.DetailedConsultingFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString @EqualsAndHashCode
@Builder
public class UpdatedConsultingDetailResponseDTO {

    private String title;
    private String writer;
    private String content;
    private List<String> routes;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime updateDate;

    public UpdatedConsultingDetailResponseDTO(Consulting saved) {

        this.title = saved.getUpdateTitle();
        this.writer = saved.getWriter();
        this.content = saved.getUpdateContent();
        this.updateDate = saved.getUpdateDate();

        this.routes = saved.getDetailedConsultingFiles()
                .stream()
                .map(DetailedConsultingFile::getRoute)
                .collect(Collectors.toList());
    }

}
