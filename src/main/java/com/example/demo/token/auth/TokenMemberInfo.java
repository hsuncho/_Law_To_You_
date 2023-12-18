package com.example.demo.token.auth;

import lombok.*;

@Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TokenMemberInfo {

    private String id;

    private String authority;
}
