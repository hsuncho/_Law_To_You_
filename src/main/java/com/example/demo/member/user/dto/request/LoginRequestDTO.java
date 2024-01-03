package com.example.demo.member.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginRequestDTO {

    private String id;

    private String password;

    private String reqAuthority;

}
