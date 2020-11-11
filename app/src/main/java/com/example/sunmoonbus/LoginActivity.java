package com.example.sunmoonbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText idEditText, pwEditText;
    RadioGroup rGroup;
    RadioButton rdoPassenger, rdoDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        findViewById(R.id.startSignUpBtn).setOnClickListener(onClickListener);
        findViewById(R.id.startFindPwBtn).setOnClickListener(onClickListener);
        idEditText = (EditText)findViewById(R.id.idEditText);
        pwEditText = (EditText)findViewById(R.id.pwEditText);
        rGroup = (RadioGroup)findViewById(R.id.rGroup);
        rdoPassenger = (RadioButton)findViewById(R.id.rdoBtnPassenger);
        rdoDriver = (RadioButton)findViewById(R.id.rdoBtnDriver);
        findViewById(R.id.rdoBtnDriver).setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnPassenger).setOnClickListener(onClickListener);
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
                case R.id.startSignUpBtn:
                    gotoActivity(SignUpActivity.class);
                    break;
                case R.id.startFindPwBtn:
                    gotoActivity(FindPwActivity.class);
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
                    if(id.equals("1")){
                        startToast("회원가입되지 않은 사용자 입니다.");
                    }else{
                        if(pw.equals("111111")){
                            startToast("로그인에 성공했습니다.");
                            gotoActivity(MainActivity.class);
                        }else{
                            startToast("비밀번호가 일치하지 않습니다.");
                        }
                    }
                    break;
                case R.id.rdoBtnDriver://기사 DB에 데이터저장
                    if(pw.equals("1")){
                        startToast("로그인에 성공했습니다.");
                        gotoActivity(MainActivity.class);
                    }else{
                        startToast("비밀번호가 일치하지 않습니다.");
                    }
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