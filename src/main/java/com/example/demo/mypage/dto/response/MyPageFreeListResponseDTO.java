package com.example.demo.mypage.dto.response;

import lombok.*;

import java.util.List;

@Getter @Setter
@EqualsAndHashCode @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MyPageFreeListResponseDTO {

    private int count;
    private List<MyPageFreeboardDetailDTO> freeboardList;

}
