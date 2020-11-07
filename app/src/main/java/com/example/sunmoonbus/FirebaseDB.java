package com.example.sunmoonbus;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FirebaseDB {
    public static FirebaseDatabase database1 = FirebaseDatabase.getInstance();
    public static DatabaseReference myRef1 = database1.getReference("BusList");
    public static HashMap<String, BusInfo> busInfo = new HashMap<String, BusInfo>();

    FirebaseDB() {
        this.init();
    }

    public static void init() {

        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String busID = data.getKey();

                    busInfo.put(busID, new BusInfo());

                    for (DataSnapshot data1 : data.getChildren()) {
                        switch (data1.getKey()) {
                            case "Desti" :
                                busInfo.get(busID).Destination = data1.getValue(String.class);
                                break;
                            case "location" :
                                busInfo.get(busID).location = data1.getValue(String.class);
                                break;
                            case "userCount" :
                                busInfo.get(busID).userCount = data1.getValue(Integer.class);
                                break;
                            default:
                                break;
                        }
                    }
                }
                //Log.d(TAG, "Value is: " + value);
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
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String busID = data.getKey();
                    for (DataSnapshot data1 : data.getChildren()) {
                        switch (data1.getKey()) {
                            case "Desti" :
                                busInfo.get(busID).Destination = data1.getValue(String.class);
                                break;
                            case "location" :
                                busInfo.get(busID).location = data1.getValue(String.class);
                                break;
                            case "userCount" :
                                busInfo.get(busID).userCount = data1.getValue(Integer.class);
                                break;
                            default:
                                break;
                        }
                    }
                }
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
