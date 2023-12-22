package com.example.demo.freeboard.dto.request;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.entity.FreeboardFile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class FreeboardUpdateRequestDTO {


    private int bno;
    private String userId;
    private String title;
    private String content;
    private List<String> routes;



    public FreeboardUpdateRequestDTO(Freeboard freeboard) {
        this.bno = freeboard.getBno();
        this.userId = freeboard.getUser().getId();
        this.title = freeboard.getTitle();
        this.content = freeboard.getContent();
        this.routes = freeboard.getFreeboardFiles().stream()
                .map(FreeboardFile::getRoute)
                .collect(Collectors.toList());
    }
}
