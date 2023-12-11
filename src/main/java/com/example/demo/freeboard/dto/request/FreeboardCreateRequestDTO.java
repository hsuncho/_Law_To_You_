package com.example.demo.freeboard.dto.request;


import com.example.demo.freeboard.entity.Freeboard;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeboardCreateRequestDTO {

    @NotBlank
    private int bno;

    @NotBlank
    private String writer;

    @NotBlank
    private String content;

    @NotBlank
    private LocalDateTime regDate;

    @NotBlank
    private String title;



}
