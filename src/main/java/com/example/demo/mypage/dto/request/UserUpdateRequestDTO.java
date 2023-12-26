package com.example.demo.mypage.dto.request;

import com.example.demo.member.user.entity.User;
import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserUpdateRequestDTO {

    private String id;

    private String password;

    private String nickname;

    public User toEntity(User foundUser) {
        return User.builder()
                .id(this.id)
                .nickname(this.nickname)
                .password(this.password)
                .email(foundUser.getEmail())
                .joinMethod(foundUser.getJoinMethod())
                .authority(foundUser.getAuthority())
                .hammer(foundUser.getHammer())
                .build();

    }

}
