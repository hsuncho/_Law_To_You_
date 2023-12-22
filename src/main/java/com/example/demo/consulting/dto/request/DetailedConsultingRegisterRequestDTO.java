package com.example.demo.consulting.dto.request;

import com.example.demo.consulting.entity.Consulting;
import com.example.demo.member.user.entity.User;
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
