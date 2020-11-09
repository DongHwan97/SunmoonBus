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

    public User user = new User();

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
                if (snapshot.exists()) {
                    user = new User(id, snapshot.child("password").getValue(String.class));
                } else {
                    user = new User();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return user;
    }



    //회원가입 완료시키기
    /*public void upUserInfo(User userInfo) {
        User user = userInfo;

        myRef.child(user.id).child("password").setValue(user.pw);
        myRef.child(user.id).child("onBus").setValue("none");

    }*/

}
