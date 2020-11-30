package com.example.sunmoonbus;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;

public class ShuttleDriverActivity extends AppCompatActivity {
    private TextView userCnt = null;
    private String onBus = null;
    private Button pressBtn = null;
    private boolean once = false;

    ValueHandler handler = new ValueHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver_people);

        userCnt = findViewById(R.id.count_textView);
        findViewById(R.id.minus_button).setOnClickListener(onClickListener);
        findViewById(R.id.plus_button).setOnClickListener(onClickListener);
        (pressBtn = findViewById(R.id.pressedCnt)).setOnClickListener(onClickListener);
        findViewById(R.id.count_textView).setOnClickListener(onClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //승자중인지?
        if (!ShuttleDBConnect.accountInfo.onBus.equals("none")) {
            this.onBus = ShuttleDBConnect.accountInfo.onBus;
            userCnt.setTextColor(Color.WHITE);
            //SunmoonUtil.startToast(this, " " + onBus + "승차중");
            Background thread = new Background();
            thread.setDaemon(true);
            thread.start();
            return;
        }
        SunmoonUtil.startToast(this, "운행중인 버스가 없습니다.");
        finish();
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            if (!SunmoonUtil.getNetStatus(ShuttleDriverActivity.this)) {
                return;
            }
            switch (view.getId()){
                case R.id.minus_button:
                    if (0 == ShuttleDBConnect.busInfo.get(onBus).userCount) {
                        SunmoonUtil.startToast(ShuttleDriverActivity.this, "0명 미만은 조정 불가합닌다.");
                        break;
                    }
                    ShuttleDBConnect.myRef1.child(onBus)
                            .child("userCount")
                            .setValue(ShuttleDBConnect.busInfo.get(onBus).userCount - 1);
                    break;

                case R.id.plus_button:
                    if (60 == ShuttleDBConnect.busInfo.get(onBus).userCount) {
                        SunmoonUtil.startToast(ShuttleDriverActivity.this, "최대 60명까지 조정 가능합니다.");
                        break;
                    }
                    ShuttleDBConnect.myRef1.child(onBus)
                            .child("userCount")
                            .setValue(ShuttleDBConnect.busInfo.get(onBus).userCount + 1);
                    break;

                case R.id.pressedCnt:
                case R.id.count_textView:
                    once = false;
                    ShuttleDBConnect.myRef1.child(onBus)
                            .child("alrm")
                            .setValue(0);
                    break;

                default:
                    break;
            }
        }
    };

    public void startSound() {
        if (!once) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            once = true;
        }
    }

    class Background extends Thread {
        public void run() {
            while(onBus != null) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("value", ShuttleDBConnect.busInfo.get(onBus).userCount);
                bundle.putInt("value1", ShuttleDBConnect.busInfo.get(onBus).alrm);
                message.setData(bundle);
                handler.sendMessage(message);
                try {
                    Thread.sleep(300);
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
            int value1 = bundle.getInt("value1");
            userCnt.setText("" + value);
            pressBtn.setText("하차할 인원 " + value1 + "명");

            if (1 <= ShuttleDBConnect.busInfo.get(onBus).alrm ) {
                startSound();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    findViewById(R.id.alrmLayout).setForeground(new ColorDrawable(0Xaaff4444));
                } else {
                    findViewById(R.id.loginlayout).setBackgroundDrawable(new ColorDrawable(0Xaaff4444));
                }
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    findViewById(R.id.alrmLayout).setForeground(null);
                } else {
                    findViewById(R.id.loginlayout).setBackgroundDrawable(null);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}