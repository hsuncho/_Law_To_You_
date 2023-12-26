package com.example.demo.freeboard.dto.response;

import com.example.demo.faq.dto.FAQMiddleAndQMSDTO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@Builder
@Slf4j
public class FreeboardDetaileResponseCountDTO {

    private int count;

    private List<FreeboardDetailResponseDTO> freeboardDetailResponseDTOS = new ArrayList<>();

    public FreeboardDetaileResponseCountDTO(int count, List<FreeboardDetailResponseDTO> freeboardDetailResponseDTOS) {
        this.count = count;
        this.freeboardDetailResponseDTOS.addAll(freeboardDetailResponseDTOS);
    }

}
