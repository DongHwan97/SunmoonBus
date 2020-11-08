package com.example.sunmoonbus;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FirebaseDB2 {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public boolean exist = false;

    FirebaseDB2() {
        //this.init();
    }

    FirebaseDB2(String path) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(path);
    }

    //아이디 중복 방지
    public boolean isIdExist(String userID){
        final String id = userID;
        exist = false;
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                exist = snapshot.child(id).hasChildren();
                System.out.println(snapshot.child(id).child("password").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }

        return exist;
    }

    public boolean isPwMatch(String userID, String userPW){
        final String id = userID;
        final String pw = userPW;
        exist = false;
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (pw.equals(snapshot.child(id).child("password").getValue(String.class))) {
                     exist = true;
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }

        return exist;
    }



    //회원가입 완료시키기
    /*public void upUserInfo(User userInfo) {
        User user = userInfo;

        myRef.child(user.id).child("password").setValue(user.pw);
        myRef.child(user.id).child("onBus").setValue("none");

    }*/

}
