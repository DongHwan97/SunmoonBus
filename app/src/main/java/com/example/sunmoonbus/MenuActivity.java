package com.example.sunmoonbus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    ImageView nfc_image, buslist_image, menu_schedule, map_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        nfc_image = (ImageView) findViewById(R.id.nfc_image);
        buslist_image = (ImageView) findViewById(R.id.buslist_image);
        menu_schedule = (ImageView) findViewById(R.id.schedule_image);
        map_image = (ImageView) findViewById(R.id.map_image);
    }

    ImageView.OnClickListener onClickListener = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.nfc_image:
                    changeActivity(TaggingActivity.class);
                    break;
                case R.id.buslist_image:
                    //changeActivity();
                    break;
                case R.id.schedule_image:
                    //changeActivity();
                    break;
                case R.id.map_image:
                    //changeAcitivity();
                    break;
                default:
                    break;
            }
        }
    };

    private void changeActivity(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivityForResult(intent, 0);
    }
}