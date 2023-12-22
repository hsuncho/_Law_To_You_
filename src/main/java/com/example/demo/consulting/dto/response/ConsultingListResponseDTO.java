package com.example.demo.consulting.dto.response;

import com.example.demo.consulting.entity.Consulting;
import com.example.demo.freeboard.dto.response.PageResponseDTO;
import lombok.*;

import java.util.List;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ConsultingListResponseDTO {

    private int count; // 총 온라인 상담 글 수
    private PageResponseDTO pageInfo;
    private List<ConsultingDetailResponseDTO> consultingList;

}
