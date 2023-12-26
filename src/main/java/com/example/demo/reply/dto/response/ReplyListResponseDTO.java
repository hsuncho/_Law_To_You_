package com.example.demo.reply.dto.response;

import com.example.demo.consulting.dto.response.ConsultingDetailResponseDTO;
import com.example.demo.freeboard.dto.response.PageResponseDTO;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyListResponseDTO {

    private int count; // 총 온라인 상담 글 수
    private List<ReplyDetailResponseDTO> replyList;

}