package com.example.demo.consulting.dto.request;

import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DetailedConsultingRegisterRequestDTO {

    private int consultNum;

    private String title;

    private String content;

}
