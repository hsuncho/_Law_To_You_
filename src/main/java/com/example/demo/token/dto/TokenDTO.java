package com.example.demo.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenDTO {

    private String accessToken;

    private String refreshToken;

}
