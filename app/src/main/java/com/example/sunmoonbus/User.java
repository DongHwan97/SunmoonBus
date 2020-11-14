package com.example.sunmoonbus;

public class User {
    public String id;
    private String pw;
    public String onBus;

    User() {
        this.id = null;
        this.pw = null;
        this.onBus = null;
    }
    User(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

    public String getPW() {
        return this.pw;
    }

}
