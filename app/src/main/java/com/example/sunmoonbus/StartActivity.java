package com.example.sunmoonbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findViewById(R.id.startLoginBtn).setOnClickListener(onClickListener);
        findViewById(R.id.startSignUpBtn).setOnClickListener(onClickListener);
        findViewById(R.id.startFindPWbtn).setOnClickListener(onClickListener);

        new FirebaseDB();
        gotoActivity(TaggingActivity.class);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.startLoginBtn:
                    gotoActivity(LoginActivity.class);
                    break;
                case R.id.startSignUpBtn:
                    gotoActivity(SignUpActivity.class);
                    break;
                case R.id.startFindPWbtn:
                    gotoActivity(FindPWActivity.class);
                    break;
            }
        }
    };
    private void gotoActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}