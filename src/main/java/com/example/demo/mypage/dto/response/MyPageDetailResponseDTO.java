package com.example.demo.mypage.dto.response;

import com.example.demo.consulting.entity.Consulting;
import com.example.demo.consulting.entity.ConsultingFile;
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
public class MyPageDetailResponseDTO {

    private String title;

    private String writer;

    private String content;

    private List<String> routes;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime date;
    
    // 수정되었는지의 여부 -> 등록 페이지로 이동할 건지 상세보기 페이지로 이동할 건지 판단
    private boolean ifUpdated;

    public MyPageDetailResponseDTO(Consulting consulting) {

        // 깊은 상담글 등록이 진행되기 전이라면
        if(consulting.getUpdateDate() == null) {
            this.title = consulting.getTitle();
            this.writer = consulting.getWriter();
            this.content = consulting.getContent();
            this.routes = consulting.getConsultingFiles()
                    .stream()
                    .map(ConsultingFile::getRoute)
                    .collect(Collectors.toList());
            this.date = consulting.getRegDate();
            this.ifUpdated = false;
        } else {
            this.title = consulting.getUpdateTitle();
            this.writer = consulting.getWriter();
            this.content = consulting.getUpdateContent();
            this.routes = consulting.getDetailedConsultingFiles()
                    .stream()
                    .map(DetailedConsultingFile::getRoute)
                    .collect(Collectors.toList());
            this.date = consulting.getUpdateDate();
            this.ifUpdated = true;
        }
    }


}
