package com.example.demo.faq.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQListResponseDTO {

    private int count;
    private PageResponseDTO pageInfo;
    private List<FAQDetailResponseDTO> posts;

}
