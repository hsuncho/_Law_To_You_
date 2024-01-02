package com.example.demo.member.master.dto.response;

import com.example.demo.faq.dto.FAQMiddleSecAndSubjectDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class ListResponseDTO {

    private final int count;
    private List<lawyerListResponseDTO> lawyerListResponseDTOS = new ArrayList<>();

    public ListResponseDTO(int count, List<lawyerListResponseDTO> lawyerListResponseDTOS) {

        this.count = count;
        this.lawyerListResponseDTOS.addAll(lawyerListResponseDTOS);

    }
}
