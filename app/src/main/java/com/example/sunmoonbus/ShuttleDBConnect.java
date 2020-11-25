package com.example.sunmoonbus;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ShuttleDBConnect {
    public static FirebaseDatabase database1 = FirebaseDatabase.getInstance();
    public static DatabaseReference myRef1 = database1.getReference("BusList");
    public static HashMap<String, BusInfo> busInfo = new HashMap<String, BusInfo>();
    public static AccountInfo accountInfo;

    ShuttleDBConnect() {
        this.init();
    }

    public static void init() {

        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String busID = data.getKey();

                    busInfo.put(busID, new BusInfo());

                    for (DataSnapshot data1 : data.getChildren()) {
                        busInfo.get(busID).upDateInfo(data1.getKey(), data1.getValue(Object.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String busID = data.getKey();

                    for (DataSnapshot data1 : data.getChildren()) {
                        busInfo.get(busID).upDateInfo(data1.getKey(), data1.getValue(Object.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
