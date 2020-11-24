package com.example.sunmoonbus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    ImageView nfc_image, buslist_image, menu_schedule, map_image, logout_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        nfc_image = (ImageView) findViewById(R.id.nfc_image);
        buslist_image = (ImageView) findViewById(R.id.buslist_image);
        menu_schedule = (ImageView) findViewById(R.id.schedule_image);
        map_image = (ImageView) findViewById(R.id.map_image);
        logout_image = (ImageView) findViewById(R.id.logout_image);

        registerForContextMenu(logout_image);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == logout_image)
            getMenuInflater().inflate(R.menu.logout_menu, menu);
    }
    /*
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.version_info:
                startActivity(new Intent())
        }
    }*/

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