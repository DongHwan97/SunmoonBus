package com.example.sunmoonbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (!ShuttleDBConnect.accountInfo.student) {
            ((ImageView) findViewById(R.id.map_image)).setImageResource(R.drawable.menu_management);
        }

        findViewById(R.id.menu_image).setOnClickListener(onClickListener);
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
                case R.id.menu_image:
                    pop(view);
                    break;

                case R.id.nfc_image:
                    startActivity(new Intent(MainActivity.this,
                            TaggingActivity.class));
                    break;

                case R.id.buslist_image:
                    startActivity(new Intent(MainActivity.this,
                            ListActivity.class));
                    break;

                case R.id.schedule_image:
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://lily.sunmoon.ac.kr/Page/About/About08_04_02_01_01_01.aspx")));
                    break;

                case R.id.map_image:
                    if (!ShuttleDBConnect.accountInfo.student) {//기사
                        if (ShuttleDBConnect.accountInfo.onBus.equals("none")) {
                            SunmoonUtil.startToast(MainActivity.this, "운행중인 버스가 없습니다.");
                            break;
                        }
                        startActivity(new Intent(MainActivity.this,
                                ShuttleDriverActivity.class));
                        break;
                    }
                    //학생
                    startActivity(new Intent(MainActivity.this,
                            MapActivity.class));
                    break;

                default:
                    break;
            }
        }
    };

    public void pop(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.logout_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.version_info:
                        SunmoonUtil.startToast(MainActivity.this, "ver 0.1a");
                        break;
                    case R.id.logout:
                        Intent intent = new Intent();
                        intent.putExtra("id", ShuttleDBConnect.accountInfo.id);
                        setResult(RESULT_OK, intent);
                        finish();
                        //SunmoonUtil.startToast(MainActivity.this, "로그아웃!");
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}