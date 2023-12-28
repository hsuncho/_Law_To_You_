package com.example.demo.freeboard.dto.response;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.entity.FreeboardFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@EqualsAndHashCode @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FreeboardCreateResponseDTO {

    private String title;
    private String writer;
    private String content;
    private List<String> routes;

    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime regDate;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime updateDate;

    public FreeboardCreateResponseDTO(Freeboard saved) {
        this.title = saved.getTitle();
        this.content = saved.getContent();
        this.writer = saved.getWriter();
        this.regDate = saved.getRegDate();
        this.routes = saved.getFreeboardFiles().stream()
                .map(FreeboardFile::getRoute)
                .collect(Collectors.toList());
    }

}
