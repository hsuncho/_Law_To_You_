package com.example.demo.member.user.dto.response;

import com.example.demo.member.Member;
import com.example.demo.token.dto.TokenDTO;
import lombok.*;

@Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String id;

    private String accessToken;

    private String authority;

    public LoginResponseDTO(Member member, TokenDTO tokenDTO) {
        this.id = member.getId();
        this.authority = member.getAuthority();
        this.accessToken = tokenDTO.getAccessToken();
    }

}
