package com.example.sunmoonbus;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaggingActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView userCnt;
    String onBus = null;
    ValueHandler handler = new ValueHandler();

    private TextView tagId1;

    FirebaseDB2 userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        userDB = new FirebaseDB2("User");

        userCnt = (TextView) findViewById(R.id.userCnt);
        userCnt.setTextColor(Color.GRAY);

        tagId1 = (TextView) findViewById(R.id.tagId);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //승자중인지?
        if (!FirebaseDB.user.onBus.equals("none")) {
            this.onBus = FirebaseDB.user.onBus;
            userCnt.setTextColor(Color.WHITE);
            Toast.makeText(this, onBus + "승차중", Toast.LENGTH_SHORT).show();
            Background thread = new Background();
            thread.setDaemon(true);
            thread.start();
        }
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            byte[] tagId = tag.getId();
            String taggedID = SunmoonUtil.toHexString(tagId);

            if (!FirebaseDB.busInfo.containsKey(taggedID)) {//등록되지 않은 버스
                Toast.makeText(this, "등록되지 않은 버스", Toast.LENGTH_SHORT).show();
                return;
            }
            Background thread = new Background();
            thread.setDaemon(true);
            tagId1.setText("tagid : "+ taggedID);
            if (onBus == null) {
                onBus = taggedID;
                userCnt.setTextColor(Color.WHITE);
                //Toast.makeText(this, taggedID + "승차", Toast.LENGTH_SHORT).show();
                if (FirebaseDB.user.student) {
                    FirebaseDB.myRef1.child(onBus).child("userCount").setValue(FirebaseDB.busInfo.get(onBus).userCount + 1);
                }
                thread.start();

                //탑승기록 - 승차
                userDB.upBusRecord(FirebaseDB.user, onBus, "ON");

            } else {
                if (onBus.equals(taggedID)) {
                    userCnt.setTextColor(Color.GRAY);
                    userCnt.setText("0/45");
                    //Toast.makeText(this, taggedID + "하차", Toast.LENGTH_SHORT).show();
                    if (FirebaseDB.user.student) {
                        FirebaseDB.myRef1.child(onBus).child("userCount").setValue(FirebaseDB.busInfo.get(onBus).userCount - 1);
                    }

                    //탑승기록 - 하차
                    userDB.upBusRecord(FirebaseDB.user, onBus, "OFF");
                    onBus = null;
                } else {
                    Toast.makeText(this, "잘못된 하차", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    class Background extends Thread {
        public void run() {
            while(onBus != null) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("value", FirebaseDB.busInfo.get(onBus).userCount);
                message.setData(bundle);
                handler.sendMessage(message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                } catch (NullPointerException e) {
                }
            }
        }
    }

    class ValueHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int value = bundle.getInt("value");
            userCnt.setText(value + "/45");
        }
    }
}