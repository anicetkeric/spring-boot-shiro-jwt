package com.boottech.springshirojwt.common;

import org.apache.shiro.authc.AuthenticationToken;

public class JWTAuthToken implements AuthenticationToken {

    private static final long serialVersionUID = 1L;

    private final String token;

    public JWTAuthToken(String token){
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}