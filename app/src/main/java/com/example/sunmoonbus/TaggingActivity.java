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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
    private boolean once = false;

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

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //승자중인지?
        if (!this.onBus.equals("none")) {

            //버스 운행 취소되면 강제로 내려짐
            if (!checkIsMoving(this.onBus)) {
                this.hideSystemUI();
                return;
            }

            //태그다음 화면 구성
            this.initView();

            SunmoonUtil.startToast(this, "버스 탑승 중 입니다.");
            Background thread = new Background();
            thread.setDaemon(true);
            thread.start();
            return;
        }
        this.hideSystemUI();
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

                SunmoonUtil.startToast(this, "승차 했습니다.");
            } else {
                SunmoonUtil.startToast(this, "안전운전 부탁드립니다.");
            }

            this.initView();//태그다음 화면 구성
            thread.start();

            //탑승기록 - 승차
            userDB.upBusRecord(ShuttleDBConnect.accountInfo, onBus, "ON");
            ShuttleDBConnect.accountInfo.onBus = onBus;

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

    //하차벨, 강제하차 기능
    View.OnTouchListener onLongTouch = new View.OnTouchListener() {
        final long bell_sec = 3000L;//3초
        final long bus_sec = 7000L;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.pressBtn:
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        then = System.currentTimeMillis();
                        bell.setBackgroundResource(R.drawable.press_bell);
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP){
                        bell.setBackgroundResource(R.drawable.bell);
                        if (once) {
                            SunmoonUtil.startToast(TaggingActivity.this, "이미 눌렀습니다.");
                            return false;
                        }
                        if((System.currentTimeMillis() - then) > bell_sec){
                            SunmoonUtil.startToast(TaggingActivity.this, "삑-");
                            ShuttleDBConnect.myRef1
                                    .child(onBus)
                                    .child("alrm")
                                    .setValue( ShuttleDBConnect.busInfo.get(onBus).alrm + 1);
                            bell.setBackgroundResource(R.drawable.bell);
                            once = true;
                            return true;
                        }
                        SunmoonUtil.startToast(TaggingActivity.this, bell_sec/1000+ "초 이상 눌렀다 때 주세요.");
                    }
                    else {
                        SunmoonUtil.startToast(TaggingActivity.this, ""
                                + (int) (System.currentTimeMillis() - then)/1000);
                    }
                    break;

                case R.id.bus:
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        then = System.currentTimeMillis();
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP){
                        if((System.currentTimeMillis() - then) > bus_sec){
                            SunmoonUtil.startToast(TaggingActivity.this, "강제로 하차합니다.");
                            userDB.upBusRecord(ShuttleDBConnect.accountInfo, onBus, "OFF_F");
                            onBus = "none";
                            ShuttleDBConnect.accountInfo.onBus = "none";
                            finish();
                            return true;
                        }
                        SunmoonUtil.startToast(TaggingActivity.this, bus_sec/1000+ "초 이상 눌렀다 때 주세요.");
                    }
                    else {
                        SunmoonUtil.startToast(TaggingActivity.this, ""
                                + (int) (System.currentTimeMillis() - then)/1000);
                    }
                    break;
            }

            return false;
        }
    };

    //기사님 행선지 선택
    RadioButton.OnClickListener onRadioSB = new RadioButton.OnClickListener() {

        @Override
        public void onClick(View view) {
            try {
                RadioButton radio = (RadioButton) view;
                ShuttleDBConnect.setDesti(radio.getText().toString(), onBus);
            } catch (ClassCastException e) {}

        }
    };

    private boolean checkIsMoving(String onBusID) {
        if (!ShuttleDBConnect.busInfo.get(onBusID).moving) {
            //SunmoonUtil.startToast(TaggingActivity.this, "운행 중지된 버스입니다.");
            userDB.upBusRecord(ShuttleDBConnect.accountInfo, onBus, "OFF_X");
            onBus = "none";
            ShuttleDBConnect.accountInfo.onBus = "none";
            return false;
        }
        return true;
    }

    private void initView() {
        this.showSystemUI();
        //화면 뷰 바꾸기
        setContentView(R.layout.activity_tag);

        //학생메뉴, 기사메뉴
        RelativeLayout menu[] = new RelativeLayout[2];
        menu[0] = findViewById(R.id.student_menu);
        menu[1] = findViewById(R.id.busdriver_menu);

        //인원 수 흰색
        userCnt = findViewById(R.id.userCnt);

        //하차벨, 버스에 리스너
        bell = findViewById(R.id.pressBtn); bus = findViewById(R.id.bus);
        bell.setOnTouchListener(onLongTouch); bus.setOnTouchListener(onLongTouch);

        //행선지 선택
        RadioButton radio_Desti[] = new RadioButton[4];
        (radio_Desti[0] = findViewById(R.id.radioBtn_1)).setOnClickListener(onRadioSB); //천안/아산역
        (radio_Desti[1] = findViewById(R.id.radioBtn_2)).setOnClickListener(onRadioSB); //천안캠퍼스
        (radio_Desti[2] = findViewById(R.id.radioBtn_3)).setOnClickListener(onRadioSB); //천안터미널
        (radio_Desti[3] = findViewById(R.id.radioBtn_4)).setOnClickListener(onRadioSB); //온양역/터미널

        switch(ShuttleDBConnect.busInfo.get(onBus).destination) {
            case "천안/아산역":
                radio_Desti[0].setChecked(true);
                break;

            case "천안/천안캠퍼스":
                radio_Desti[1].setChecked(true);
                break;

            case "천안터미널":
                radio_Desti[2].setChecked(true);
                break;

            case "온양역/터미널":
                radio_Desti[3].setChecked(true);
                break;

            default:
                break;
        }

        //기사메뉴 구성
        if (!ShuttleDBConnect.accountInfo.student) {
            menu[0].setVisibility(View.GONE);
            menu[1].setVisibility(View.VISIBLE);
            return;
        }
        //학생메뉴구성
        menu[0].setVisibility(View.VISIBLE);
        menu[1].setVisibility(View.GONE);
    }

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
                bundle.putString("value1", ShuttleDBConnect.busInfo.get(onBus).destination);
                //알람 한번만 가능함
                if (0 == ShuttleDBConnect.busInfo.get(onBus).alrm) {
                    once = false;
                }
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
            String value1 = bundle.getString("value1");
            userCnt.setText(value1 + "\n" + value + "/45");
        }
    }
}