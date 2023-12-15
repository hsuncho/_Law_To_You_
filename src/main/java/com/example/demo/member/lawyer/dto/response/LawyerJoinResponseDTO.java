package com.example.demo.member.lawyer.dto.response;

import com.example.demo.member.lawyer.entity.Lawyer;
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
}
