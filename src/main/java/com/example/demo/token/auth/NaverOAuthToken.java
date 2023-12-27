package com.example.demo.token.auth;

import lombok.Getter;

public class NaverOAuthToken {
    @Getter
    private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;

    public NaverOAuthToken() {
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }
}
