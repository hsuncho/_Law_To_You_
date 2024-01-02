package com.example.demo.freeboard.dto.response;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.entity.FreeboardFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeboardDetailResponseDTO {

    // 자유게시판 클릭시 글번호, 제목, 작성자, 작성일자, 리스트 요청
    private int TrueFalse;
    private int bno;
    private String title;
    private String writer;
    private String content;
    private List<String> routes;


    @JsonFormat(pattern = "yyyy/MM/dd") // 제이슨으로 가져올 때 이 타입으로 매핑해서 줌 이 라이브러리 사용
    private LocalDateTime regDate;

    // 상세보기시
    public FreeboardDetailResponseDTO(int trueFalse, Freeboard freeboard) {
        this.TrueFalse = trueFalse;
        this.title = freeboard.getTitle();
        this.writer = freeboard.getWriter();
        this.content = freeboard.getContent();
        this.regDate = freeboard.getRegDate();

        this.routes = freeboard.getFreeboardFiles().stream()
                .map(FreeboardFile::getRoute)
                .collect(Collectors.toList());
    }

    // 검색시
    public FreeboardDetailResponseDTO(String bool, Freeboard freeboard) {
        this.bno = freeboard.getBno();
        this.title = freeboard.getTitle();
        this.writer = freeboard.getWriter();
        this.regDate = freeboard.getRegDate();

    }

    // 수정시 필요한 생성자
    public FreeboardDetailResponseDTO(Freeboard freeboard) {
        this.title = freeboard.getTitle();
        this.content = freeboard.getContent();
        this.regDate = freeboard.getRegDate();
        this.routes = freeboard.getFreeboardFiles().stream()
                .map(FreeboardFile::getRoute)
                .collect(Collectors.toList());
    }

}
