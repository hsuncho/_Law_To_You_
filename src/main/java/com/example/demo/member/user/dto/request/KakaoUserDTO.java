package com.example.demo.member.user.dto.request;

import com.example.demo.member.user.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
public class KakaoUserDTO {

    private long id;

    @JsonProperty("connected_at")
    private LocalDateTime connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Setter @Getter @ToString
    public static class KakaoAccount {

        private String email;

        private Profile profile;

        @Getter @Setter @ToString
        public static class Profile {
            private String nickname;

        }
    }

    public User toEntity (String accessToken) {
        return User.builder()
                .id(String.valueOf(this.id)) // user의 id는 String, kakao에서 제공된 id는 Long 타입
                .email(this.kakaoAccount.getEmail())
                .nickname(this.kakaoAccount.profile.nickname+"("+String.valueOf(this.id)+")")
                .password("password!")
                .joinMethod("kakao")
                .accessToken(accessToken)
                .build();
    }

}
