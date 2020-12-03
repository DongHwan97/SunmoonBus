package com.smu.sunmoonbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ListActivity extends AppCompatActivity {
    ImageView img[] = new ImageView[4];
    Button btn1,btn2,btn3,btn4;
    TextView textView[] = new TextView[4];
    TextView des_text[] = new TextView[4];
    TextView light[] = new TextView[4];
    BusInfo businfoT[] = new BusInfo[4];

    boolean th = false;

    ValueHandler handler = new ValueHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        img[0]=findViewById(R.id.asan_waiting);
        img[1]=findViewById(R.id.cheonan_cam_waiting);
        img[2]=findViewById(R.id.cheon_terminal_waiting);
        img[3]=findViewById(R.id.on_terminal_waiting);

        textView[0]=findViewById(R.id.count_text1);
        textView[1]=findViewById(R.id.count_text2);
        textView[2]=findViewById(R.id.count_text3);
        textView[3]=findViewById(R.id.count_text4);

        des_text[0]=findViewById(R.id.des_text1);
        des_text[1]=findViewById(R.id.des_text2);
        des_text[2]=findViewById(R.id.des_text3);
        des_text[3]=findViewById(R.id.des_text4);

        light[0]=findViewById(R.id.light1);
        light[1]=findViewById(R.id.light2);
        light[2]=findViewById(R.id.light3);
        light[3]=findViewById(R.id.light4);

        findViewById(R.id.button1).setOnClickListener(toGoBus);
        findViewById(R.id.button2).setOnClickListener(toGoBus);
        findViewById(R.id.button3).setOnClickListener(toGoBus);
        findViewById(R.id.button4).setOnClickListener(toGoBus);

        th =  true;

        int i = 0;
        for (BusInfo businfo : ShuttleDBConnect.busInfo.values()) {
            businfoT[i] = businfo;

            des_text[i].setText(businfoT[i].destination);
            textView[i].setText("" + businfoT[i].userCount);

            img[i].setVisibility((businfoT[i].moving) ? View.INVISIBLE : View.VISIBLE);

            i++;
        }

        Background thread = new Background();
        thread.setDaemon(true);
        thread.start();
    }

    Button.OnClickListener toGoBus = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            int index = 0;
            Intent intent =  new Intent(ListActivity.this, MapActivity.class);
            switch (view.getId()){
                case R.id.button1: index = 0; break;
                case R.id.button2: index = 1; break;
                case R.id.button3: index = 2; break;
                case R.id.button4: index = 3; break;
            }

            if (!businfoT[index].moving) {
                SunmoonUtil.startToast(ListActivity.this, "해당 버스는 운행중이 아닙니다.\n(마지막 위치로 갑니다)");
            }
            intent.putExtra("SeletedBusLa", businfoT[index].latitude);
            intent.putExtra("SeletedBusLo", businfoT[index].longitude);
            intent.putExtra("Zoom", 3);
            startActivity(intent);
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        th = false;
        finish();
    }
    
    class Background extends Thread {
        public void run() {
            while(th) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();

                int value[] = {
                        businfoT[0].userCount,
                        businfoT[1].userCount,
                        businfoT[2].userCount,
                        businfoT[3].userCount
                };
                String svalue[] = {
                        businfoT[0].destination,
                        businfoT[1].destination,
                        businfoT[2].destination,
                        businfoT[3].destination
                };

                bundle.putIntArray("UserCount", value);
                bundle.putStringArray("Destination", svalue);

                message.setData(bundle);
                handler.sendMessage(message);
                try {
                    Thread.sleep(800);
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

            int value[] = bundle.getIntArray("UserCount");
            String svalue[] = bundle.getStringArray("Destination");

            for (int i= 0; i < value.length; i++) {
                img[i].setVisibility((businfoT[i].moving) ? View.INVISIBLE : View.VISIBLE);

                textView[i].setText("" + value[i]);
                des_text[i].setText(svalue[i]);

                if(40 <= value[i]) {
                    light[i].setBackgroundColor(0xFFF44336);
                }
                else if (25 <= value[i]) {
                    light[i].setBackgroundColor(0xFFFFC107);
                }
                else {
                    light[i].setBackgroundColor(0xFF4CAF50);
                }
            }
        }
    }
}