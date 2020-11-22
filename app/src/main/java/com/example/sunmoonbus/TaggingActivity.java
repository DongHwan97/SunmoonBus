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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TaggingActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView userCnt;
    String onBus = null;
    ValueHandler handler = new ValueHandler();

    private TextView tagId1;

    AccountDBConnect userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        userDB = new AccountDBConnect("User");

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
        if (!ShuttleDBConnect.accountInfo.onBus.equals("none")) {
            this.onBus = ShuttleDBConnect.accountInfo.onBus;
            userCnt.setTextColor(Color.WHITE);
            SunmoonUtil.startToast(this, " " + onBus + "승차중");
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

            if (!ShuttleDBConnect.busInfo.containsKey(taggedID)) {//등록되지 않은 버스
                SunmoonUtil.startToast(this, "등록되지 않은 버스");
                return;
            }
            Background thread = new Background();
            thread.setDaemon(true);
            tagId1.setText("tagid : "+ taggedID);
            if (onBus == null) {
                onBus = taggedID;
                userCnt.setTextColor(Color.WHITE);
                if (ShuttleDBConnect.accountInfo.student) {
                    ShuttleDBConnect.myRef1.child(onBus).child("userCount").setValue(ShuttleDBConnect.busInfo.get(onBus).userCount + 1);
                }
                thread.start();

                //탑승기록 - 승차
                userDB.upBusRecord(ShuttleDBConnect.accountInfo, onBus, "ON");

            } else {
                if (onBus.equals(taggedID)) {
                    userCnt.setTextColor(Color.GRAY);
                    userCnt.setText("0/45");
                    if (ShuttleDBConnect.accountInfo.student) {
                        ShuttleDBConnect.myRef1.child(onBus).child("userCount").setValue(ShuttleDBConnect.busInfo.get(onBus).userCount - 1);
                    }

                    //탑승기록 - 하차
                    userDB.upBusRecord(ShuttleDBConnect.accountInfo, onBus, "OFF");
                    onBus = null;
                } else {
                    SunmoonUtil.startToast(this, "잘못된 하차");
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
                bundle.putInt("value", ShuttleDBConnect.busInfo.get(onBus).userCount);
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