package com.example.sunmoonbus;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ShuttleDriverActivity extends AppCompatActivity {
    TextView userCnt = null;
    String onBus = null;
    ValueHandler handler = new ValueHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver_people);

        userCnt = findViewById(R.id.count_textView);
        findViewById(R.id.minus_button).setOnClickListener(onClickListener);
        findViewById(R.id.plus_button).setOnClickListener(onClickListener);
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

                default:
                    break;
            }
        }
    };


    class Background extends Thread {
        public void run() {
            while(onBus != null) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("value", ShuttleDBConnect.busInfo.get(onBus).userCount);
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
            userCnt.setText("" + value);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}