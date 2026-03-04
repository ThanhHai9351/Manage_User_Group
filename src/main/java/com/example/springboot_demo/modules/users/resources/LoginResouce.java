package com.example.springboot_demo.modules.users.resources;

public class LoginResouce {
    private final String token;
    private final String refreshToken;
    private final UserRescource user;

    public LoginResouce(String token, String refreshToken, UserRescource user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public UserRescource getUser() {
        return user;
    }
}
