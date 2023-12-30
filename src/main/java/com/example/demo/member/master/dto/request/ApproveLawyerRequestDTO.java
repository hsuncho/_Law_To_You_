package com.example.demo.member.master.dto.request;

import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApproveLawyerRequestDTO {

    private String id;

    private String route;


}
