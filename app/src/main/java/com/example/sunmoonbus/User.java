package com.example.sunmoonbus;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String id;
    public String pw;
    public String divide;

    public User(){}
    public User(String id, String pw, String divide)
    {
        this.id=id;
        this.pw=pw;
        this.divide=divide;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDivide() {
        return divide;
    }

    public void setDivide(String divide) {
        this.divide = divide;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", divide='" + divide + '\'' +
                '}';
    }

}
