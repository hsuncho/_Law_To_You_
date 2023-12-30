package com.example.demo.mypage.dto.response;

import com.example.demo.freeboard.dto.response.PageResponseDTO;
import lombok.*;

import java.util.List;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserConsultingListResponseDTO {

    private int count;
    private List<UserConsultingDetailDTO> consultingList;
}
