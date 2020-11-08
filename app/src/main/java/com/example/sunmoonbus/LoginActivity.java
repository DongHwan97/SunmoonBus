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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText idEditText, pwEditText;
    RadioGroup rGroup;
    RadioButton rdoPassenger, rdoDriver;

    FirebaseDB2 userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        idEditText = (EditText)findViewById(R.id.idEditText);
        pwEditText = (EditText)findViewById(R.id.pwEditText);
        rGroup = (RadioGroup)findViewById(R.id.rGroup);
        rdoPassenger = (RadioButton)findViewById(R.id.rdoBtnPassenger);
        rdoDriver = (RadioButton)findViewById(R.id.rdoBtnDriver);
        findViewById(R.id.rdoBtnDriver).setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnPassenger).setOnClickListener(onClickListener);
        userDB = new FirebaseDB2("User");
    }

    public void onBackPressed(){
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.loginButton:
                    login();
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

    private void login() {
        String id = idEditText.getText().toString();
        String pw = pwEditText.getText().toString();

        if(id.length()>=10&&pw.length()>=6){
            switch(rGroup.getCheckedRadioButtonId()){
                case R.id.rdoBtnPassenger://승객
                    if(!userDB.isIdExist(id)){
                        startToast("회원가입되지 않은 사용자 입니다.");
                    }else{
                        if(userDB.isPwMatch(id, pw)){
                            startToast("로그인에 성공했습니다.");
                            gotoActivity(MainActivity.class);
                        }else{
                            startToast("비밀번호가 일치하지 않습니다.");
                        }
                    }
                    break;
                case R.id.rdoBtnDriver://기사 DB에 데이터저장
                    /*if(){
                        startToast("로그인에 성공했습니다.");
                        gotoActivity(MainActivity.class);
                    }else{
                        startToast("비밀번호가 일치하지 않습니다.");
                    }*/
                }
        }else{
            startToast("학번은 10자리이상, 비밀번호는 6자리이상입니다.");
        }
    }

    private void gotoActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}