package com.example.sunmoonbus;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FirebaseDB2 {
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private User user;

    FirebaseDB2(String path) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(path);
    }

    //아이디 중복 방지
    public User isIdExist(String userID, String type){
        final String id = userID;

        myRef.child(type).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    user = new User("NONE");
                    return;
                }

                user = new User(id
                                , snapshot.child("password").getValue(String.class)
                                , snapshot.child("phoneNumber").getValue(String.class)
                                , snapshot.child("onBus").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return user;
    }

    //회원가입 완료시키기 및 비밀번호 초기화
    public void upUserInfo(User userInfo, String type) {
        myRef.child(type).child(userInfo.id).child("password").setValue(SunmoonUtil.toSHAString(userInfo.getPW()));
        myRef.child(type).child(userInfo.id).child("onBus").setValue(userInfo.onBus);
        myRef.child(type).child(userInfo.id).child("phoneNumber").setValue(userInfo.phoneNumber);
    }

    //버스승차 기록하기
    public void upBusRecord(User userInfo, String busID, String on) {
        String type = userInfo.student ? "St" : "Bd";

        myRef.child(type)
                .child(userInfo.id)
                .child("records")
                .child(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
                .setValue(busID + "_" + on);

        myRef.child(type)
                .child(userInfo.id)
                .child("onBus")
                .setValue(on.equals("ON") ? busID : "none");

        if (!userInfo.student) {
            FirebaseDB.myRef1.child(busID).child("moving").setValue(on.equals("ON") ? true : false);
        }
    }

}
