package com.example.sunmoonbus;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.Charset;
import java.util.Locale;

public class TaggingActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView userCnt;
    private Button bell;
    private Button bus;

    String onBus;
    ValueHandler handler = new ValueHandler();
    AccountDBConnect userDB;
    long then = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDB = new AccountDBConnect("User");

        setContentView(R.layout.activity_animation_tag);

        //버스ID 또는 none
        this.onBus = ShuttleDBConnect.accountInfo.onBus;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (!ShuttleDBConnect.accountInfo.student) {
            bell.setVisibility(View.GONE);
        }

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //승자중인지?
        if (!this.onBus.equals("none")) {
            showSystemUI();
            setContentView(R.layout.activity_tag);
            userCnt = findViewById(R.id.userCnt);
            userCnt.setTextColor(Color.WHITE);
            bell = findViewById(R.id.pressBtn); bus = findViewById(R.id.bus);
            bell.setOnTouchListener(onLongTouch); bus.setOnTouchListener(onLongTouch);

            //SunmoonUtil.startToast(this, " " + onBus + "승차중");
            SunmoonUtil.startToast(this, "버스 탑승 중 입니다.");
            Background thread = new Background();
            thread.setDaemon(true);
            thread.start();
            return;
        }
        hideSystemUI();
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
    protected void onNewIntent(Intent intent) throws NullPointerException{
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag == null) {
            return;
        }

        byte[] tagId = tag.getId();
        String taggedID = SunmoonUtil.toHexString(tagId);

        if (!ShuttleDBConnect.busInfo.containsKey(taggedID)) {//등록되지 않은 버스
            SunmoonUtil.startToast(this, "등록되지 않은 버스");
            return;
        }

        Background thread = new Background();
        thread.setDaemon(true);

        if (onBus.equals("none")) {
            onBus = taggedID;

            if (ShuttleDBConnect.accountInfo.student) {
                ShuttleDBConnect.myRef1.child(onBus)
                        .child("userCount")
                        .setValue(ShuttleDBConnect.busInfo.get(onBus).userCount + 1);
            } else {
                SunmoonUtil.startToast(this, "안전운전 부탁드립니다.");
            }

            thread.start();

            showSystemUI();
            setContentView(R.layout.activity_tag);
            userCnt = (TextView) findViewById(R.id.userCnt);
            userCnt.setTextColor(Color.WHITE);
            bell = findViewById(R.id.pressBtn); bus = findViewById(R.id.bus);
            bell.setOnTouchListener(onLongTouch); bus.setOnTouchListener(onLongTouch);

            //탑승기록 - 승차
            userDB.upBusRecord(ShuttleDBConnect.accountInfo, onBus, "ON");
            ShuttleDBConnect.accountInfo.onBus = onBus;
            SunmoonUtil.startToast(this, "승차 했습니다.");

        } else {
            if (!onBus.equals(taggedID)) {
                SunmoonUtil.startToast(this, "잘못된 하차");
                return;
            }

            if (ShuttleDBConnect.accountInfo.student
                    && 0 < ShuttleDBConnect.busInfo.get(onBus).userCount) {

                ShuttleDBConnect.myRef1.child(onBus)
                        .child("userCount")
                        .setValue(ShuttleDBConnect.busInfo.get(onBus).userCount - 1);
            }
            thread.interrupt();

            //탑승기록 - 하차
            userDB.upBusRecord(ShuttleDBConnect.accountInfo, onBus, "OFF");
            onBus = "none";
            ShuttleDBConnect.accountInfo.onBus = "none";
            SunmoonUtil.startToast(this, "하차 했습니다.");
            finish();
        }
    }


    ImageView.OnTouchListener onLongTouch = new ImageView.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.pressBtn:
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        bell.setBackgroundResource(R.drawable.press_bell);
                        then = (Long) System.currentTimeMillis();
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP){
                        if(((Long) System.currentTimeMillis() - then) > 2000){
                            SunmoonUtil.startToast(TaggingActivity.this, "삑-");
                            ShuttleDBConnect.myRef1
                                    .child(onBus)
                                    .child("alrm")
                                    .setValue( ShuttleDBConnect.busInfo.get(onBus).alrm + 1);
                            bell.setBackgroundResource(R.drawable.bell);
                            return true;
                        }
                        SunmoonUtil.startToast(TaggingActivity.this, "2초 이상 눌렀다 때 주세요.");
                        bell.setBackgroundResource(R.drawable.bell);
                    }
                    break;

                case R.id.bus:
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        then = (Long) System.currentTimeMillis();
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP){
                        if(((Long) System.currentTimeMillis() - then) > 5000){
                            SunmoonUtil.startToast(TaggingActivity.this, "강제로 하차합니다.");
                            userDB.upBusRecord(ShuttleDBConnect.accountInfo, onBus, "OFF_F");
                            onBus = "none";
                            ShuttleDBConnect.accountInfo.onBus = "none";
                            finish();
                            return true;
                        }
                        SunmoonUtil.startToast(TaggingActivity.this, "5초 이상 눌렀다 때 주세요.");
                    }
                    break;
            }

            return false;
        }
    };

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    class Background extends Thread {
        public void run() {
            while(!onBus.equals("none")) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("value", ShuttleDBConnect.busInfo.get(onBus).userCount);
                message.setData(bundle);
                handler.sendMessage(message);
                try {
                    Thread.sleep(300);
                }
                catch (InterruptedException e) {}
                catch (NullPointerException e) {}
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