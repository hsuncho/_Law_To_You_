package com.example.demo.mypage.dto.request;

import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LawyerUpdateRequestDTO {

    private String id;

    private String password;


}
