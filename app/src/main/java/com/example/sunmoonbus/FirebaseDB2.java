package com.example.sunmoonbus;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class FirebaseDB2 {
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private User user = null;

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
                    user = new User("NONE", "NONE");
                    return;
                }

                user = new User(id, snapshot.child("password").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return user;
    }


    //회원가입 완료시키기
    public void upUserInfo(User userInfo, String type) {
        myRef.child(type).child(userInfo.id).child("password").setValue(SunmoonUtil.toSHAString(userInfo.getPW()));
        myRef.child(type).child(userInfo.id).child("onBus").setValue("none");
    }

}
