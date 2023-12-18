package com.example.demo.member.user.dto.response;

import com.example.demo.member.Member;
import com.example.demo.member.user.entity.User;
import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserJoinResponseDTO {

    private String id;

    private String nickname;

    private String authority;

    public UserJoinResponseDTO(User saved) {
        this.id = saved.getId();
        this.nickname = saved.getNickname();
        this.authority = saved.getAuthority();
    }

    public Member insertMember(User saved) {
       return Member.builder()
                .id(saved.getId())
                .authority(saved.getAuthority())
                .user(saved)
                .build();
    }

}
