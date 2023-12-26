package com.example.demo.mypage.dto.response;

import com.example.demo.member.user.entity.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@EqualsAndHashCode @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserDetailResponseDTO {

    @NotBlank
    private String id;

    @NotBlank
    private String nickname;

    @Email
    @NotBlank
    private String email;

    public UserDetailResponseDTO(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
    }


}
