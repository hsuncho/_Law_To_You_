package com.example.demo.member.user.dto.request;

import com.example.demo.member.user.entity.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@ToString @EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinRequestDTO {

    @NotBlank
    private String id;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String joinMethod;


    public User toEntity() {
        return User.builder()
                .id(this.id)
                .password(this.password)
                .nickname(this.nickname)
                .email(this.email)
                .joinMethod(this.joinMethod)
                .build();
    }


}
