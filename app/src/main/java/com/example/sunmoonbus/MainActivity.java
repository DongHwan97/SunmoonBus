package com.example.sunmoonbus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (!ShuttleDBConnect.accountInfo.student) {
            ((ImageView) findViewById(R.id.map_image)).setImageResource(R.drawable.menu_schedule);
        }

        findViewById(R.id.nfc_image).setOnClickListener(onClickListener);
        findViewById(R.id.buslist_image).setOnClickListener(onClickListener);
        findViewById(R.id.schedule_image).setOnClickListener(onClickListener);
        findViewById(R.id.map_image).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            if (!SunmoonUtil.getNetStatus(MainActivity.this)) {
                return;
            }

            switch (view.getId()){
                case R.id.nfc_image:
                    startActivity(new Intent(MainActivity.this,
                            TaggingActivity.class));
                    break;
                /*
                case R.id.buslist_image:
                    startActivity(new Intent(MainActivity.this,
                            BusListActivity.class));
                    break;*/

                case R.id.schedule_image:
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://lily.sunmoon.ac.kr/Page/About/About08_04_02_01_01_01.aspx")));
                    break;

                case R.id.map_image:
                    if (!ShuttleDBConnect.accountInfo.student) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://lily.sunmoon.ac.kr/Page/About/About08_04_02_01_01_01.aspx")));
                        break;
                    }
                    startActivity(new Intent(MainActivity.this,
                            TaggingActivity.class));
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}