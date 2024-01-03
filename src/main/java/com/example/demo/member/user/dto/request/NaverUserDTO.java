package com.example.demo.member.user.dto.request;

import com.example.demo.member.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NaverUserDTO {

    private String id;
    private String email;
    private String name;

    public User toEntity (String accessToken) {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .nickname(this.name + "(" + this.id.substring(0,10) + ")")
                .password("password!")
                .joinMethod("naver")
                .accessToken(accessToken)
                .build();
    }

}
