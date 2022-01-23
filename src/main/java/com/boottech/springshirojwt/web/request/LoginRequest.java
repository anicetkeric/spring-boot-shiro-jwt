package com.boottech.springshirojwt.web.request;

public class LoginRequest {

    String username;
    String password;
    Boolean rememberme;
    String code;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Boolean getRememberme() {
        return rememberme;
    }
    public void setRememberme(Boolean rememberme) {
        this.rememberme = rememberme;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
