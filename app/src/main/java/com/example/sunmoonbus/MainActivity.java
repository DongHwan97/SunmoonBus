package com.example.sunmoonbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.nfc_button).setOnClickListener(onClickListener);
        findViewById(R.id.buslist_button).setOnClickListener(onClickListener);
        findViewById(R.id.schedule_button).setOnClickListener(onClickListener);
        findViewById(R.id.button18).setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.nfc_button:

                    break;
                case R.id.buslist_button:

                    break;
                case R.id.schedule_button:

                    break;
                case R.id.button18:
                    gotoActivity(GpsActivity.class);
                    break;
            }
        }
    };
    private void gotoActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }
}