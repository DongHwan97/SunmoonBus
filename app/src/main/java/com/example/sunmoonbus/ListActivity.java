package com.example.sunmoonbus;

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

public class ListActivity extends AppCompatActivity implements View.OnClickListener{
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

        textView[0]=findViewById(R.id.asan_count);
        textView[1]=findViewById(R.id.cheonan_cam_count);
        textView[2]=findViewById(R.id.cheon_terminal_count);
        textView[3]=findViewById(R.id.on_terminal_count);

        des_text[0]=findViewById(R.id.des_text1);
        des_text[1]=findViewById(R.id.des_text2);
        des_text[2]=findViewById(R.id.des_text3);
        des_text[3]=findViewById(R.id.des_text4);

        light[0]=findViewById(R.id.light1);
        light[1]=findViewById(R.id.light2);
        light[2]=findViewById(R.id.light3);
        light[3]=findViewById(R.id.light4);

        btn1=findViewById(R.id.gotoStationBtn);
        btn1.setOnClickListener(this);
        btn2=findViewById(R.id.gotoCampusBtn);
        btn2.setOnClickListener(this);
        btn3=findViewById(R.id.gotoTerminalBtn);
        btn3.setOnClickListener(this);
        btn4=findViewById(R.id.gotoOnyangBtn);
        btn4.setOnClickListener(this);

        th =  true;

        int i = 0;

        for (BusInfo businfo : ShuttleDBConnect.busInfo.values()) {
            businfoT[i] = businfo;
            des_text[i].setText(businfoT[i].destination);
            textView[i].setText("" + businfoT[i].userCount);

            if(businfoT[i].moving) {
                img[i].setVisibility(View.INVISIBLE);
            }

            i++;
        }

        Background thread = new Background();
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gotoStationBtn:
            case R.id.gotoCampusBtn:
            case R.id.gotoTerminalBtn:
            case R.id.gotoOnyangBtn:
                gotoActivity(BusActivity.class);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        th = false;
        finish();
    }

    private void gotoActivity(Class c){
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
    
    class Background extends Thread {
        public void run() {
            while(th) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("value1", businfoT[0].userCount);
                bundle.putString("svalue1", businfoT[0].destination);

                bundle.putInt("value2", businfoT[1].userCount);
                bundle.putString("svalue2", businfoT[1].destination);

                bundle.putInt("value3", businfoT[2].userCount);
                bundle.putString("svalue3", businfoT[2].destination);



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
            int value[] = {
                    bundle.getInt("value1")
                    ,bundle.getInt("value2")
                    ,bundle.getInt("value3")
            };
            String svalue[] = {
                    bundle.getString("svalue1")
                    ,bundle.getString("svalue2")
                    ,bundle.getString("svalue3")
            };

            for (int i= 0; i < 3; i++) {
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