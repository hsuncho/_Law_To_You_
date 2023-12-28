package com.example.demo.mypage.dto.response;

import lombok.*;

import java.util.List;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LawyerConsultingListResponseDTO {

    private int count;
    private List<LawyerConsultingDetailDTO> consultingList;

}
