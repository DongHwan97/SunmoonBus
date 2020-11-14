package com.example.sunmoonbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class FindPwActivity extends AppCompatActivity {
    EditText idEditText;
    RadioGroup rGroup;
    RadioButton rdoPassenger, rdoDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        idEditText = (EditText)findViewById(R.id.idEditText);
        rGroup = (RadioGroup)findViewById(R.id.rGroup);
        rdoPassenger = (RadioButton)findViewById(R.id.rdoBtnPassenger);
        rdoDriver = (RadioButton)findViewById(R.id.rdoBtnDriver);
        findViewById(R.id.rdoBtnDriver).setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnPassenger).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loginButton:
                    send();
                    break;
                case R.id.rdoBtnPassenger:
                    idEditText.setHint("학번");
                    break;
                case R.id.rdoBtnDriver:
                    idEditText.setHint("전화번호");
                    break;
            }
        }
    };

    private void send() {
        String id = idEditText.getText().toString();


        if(id.length()>=10){

        }else{
            startToast("아이디는 10자 이상입니다.");
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void gotoActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }

}