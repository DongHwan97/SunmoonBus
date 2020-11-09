package com.example.sunmoonbus;

public class User {
    public String id;
    public String pw;
    public String onBus;

    User() {}
    User(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

    public String getPW() {
        return this.pw;
    }

}
