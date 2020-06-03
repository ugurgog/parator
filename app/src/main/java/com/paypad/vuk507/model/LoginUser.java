package com.paypad.vuk507.model;

import java.io.Serializable;

public class LoginUser implements Serializable {

    private String username;
    private String password;

    public LoginUser() {
        this.username = "";
    }

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
}
