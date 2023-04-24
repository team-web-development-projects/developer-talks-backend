package com.dtalks.dtalks.user.dto;

public class UserDto {
    private String userid;
    private String password;
    private String email;
    private String nickname;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String username) {
        this.userid = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
