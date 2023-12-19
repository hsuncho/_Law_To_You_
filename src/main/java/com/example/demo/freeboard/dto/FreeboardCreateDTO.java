package com.example.demo.freeboard.dto;

import com.example.demo.freeboard.dto.request.FreeboardCreateRequestDTO;
import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeboardCreateDTO {
    private FreeboardCreateRequestDTO requestDTO;
    private String route;
}
