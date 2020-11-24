package com.example.sunmoonbus;

public class AccountInfo {
    public String id;
    private String pw;

    public String onBus;
    public String phoneNumber;
    public Boolean student;

    AccountInfo() {
        this.id = null;
        this.pw = null;
        this.onBus = null;
    }

    AccountInfo(String id) {
        this.id = id;
    }

    //기사
    AccountInfo(String id, String pw) {
        this.id = id;
        this.pw = pw;
        this.student = false;
    }

    //학생
    AccountInfo(String id, String pw, String phoneNumber, String onBus) {
        this.id = id;
        this.pw = pw;
        this.phoneNumber = phoneNumber;
        this.onBus = onBus;
        this.student = true;
    }

    public String getPW() {
        return this.pw;
    }

}
