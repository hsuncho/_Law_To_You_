package com.example.demo.freeboard.dto;

import com.example.demo.freeboard.entity.Freeboard;
import com.example.demo.freeboard.entity.FreeboardFile;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder
public class FreeboardFileDTO {

    private String route;

    public FreeboardFile toEntity(Freeboard freeboard) {
        return FreeboardFile.builder()
                .route(this.route)
                .freeboard(freeboard)
                .build();

    }
}
