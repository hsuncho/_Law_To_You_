package com.example.demo.member.lawyer.dto.response;

import com.example.demo.member.Member;
import com.example.demo.member.lawyer.entity.Lawyer;
import com.example.demo.member.user.entity.User;
import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LawyerJoinResponseDTO {

    private String id;

    private String name;

    private String authority;

    public LawyerJoinResponseDTO(Lawyer saved) {
        this.id = saved.getLawyerId();
        this.name = saved.getName();
        this.authority = saved.getAuthority();
    }

    public Member insertMember(Lawyer saved) {
        return Member.builder()
                .id(saved.getLawyerId())
                .authority(saved.getAuthority())
                .lawyer(saved)
                .build();
    }
}
