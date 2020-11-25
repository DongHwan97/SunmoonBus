package com.example.sunmoonbus;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AccountDBConnect {
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private AccountInfo accountInfo;

    AccountDBConnect(String path) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(path);
    }

    //아이디 중복 방지
    public AccountInfo isIdExist(String userID, String type){
        final String id = userID;

        myRef.child(type).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    accountInfo = new AccountInfo("none");
                    return;
                }

                accountInfo = new AccountInfo(id
                                , snapshot.child("password").getValue(String.class)
                                , snapshot.child("phoneNumber").getValue(String.class)
                                , snapshot.child("onBus").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return accountInfo;
    }

    //회원가입 완료시키기 및 비밀번호 초기화
    public void upUserInfo(AccountInfo accountInfoInfo, String type) {
        myRef.child(type).child(accountInfoInfo.id).child("password").setValue(SunmoonUtil.toSHAString(accountInfoInfo.getPW()));
        myRef.child(type).child(accountInfoInfo.id).child("onBus").setValue(accountInfoInfo.onBus);
        myRef.child(type).child(accountInfoInfo.id).child("phoneNumber").setValue(accountInfoInfo.phoneNumber);
    }

    //버스승차 기록하기
    public void upBusRecord(AccountInfo accountInfoInfo, String busID, String on) {
        String type = accountInfoInfo.student ? "St" : "Bd";

        myRef.child(type)
                .child(accountInfoInfo.id)
                .child("records")
                .child(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
                .setValue(busID + "_" + on);

        myRef.child(type)
                .child(accountInfoInfo.id)
                .child("onBus")
                .setValue(on.equals("ON") ? busID : "none");

        //버스기사면 해당버스 운행중 적용
        if (!accountInfoInfo.student) {
            ShuttleDBConnect.myRef1.child(busID).child("moving").setValue(on.equals("ON") ? true : false);
            if (on.equals("ON")) {

            }

        }
    }


}
