package com.example.sunmoonbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText idEditText, pwEditText;
    RadioGroup rGroup;
    RadioButton rdoPassenger, rdoDriver;
    Toast toast;

    FirebaseDB2 userDB;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new FirebaseDB();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDB = new FirebaseDB2("User");

        idEditText = (EditText)findViewById(R.id.idEditText);
        pwEditText = (EditText)findViewById(R.id.pwEditText);
        rGroup = (RadioGroup)findViewById(R.id.rGroup);
        rdoPassenger = (RadioButton)findViewById(R.id.rdoBtnPassenger);
        rdoDriver = (RadioButton)findViewById(R.id.rdoBtnDriver);

        idEditText.setOnFocusChangeListener(onFocusChangePW);
        idEditText.setOnKeyListener(onKeyID);

        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnDriver).setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnPassenger).setOnClickListener(onClickListener);

        findViewById(R.id.registButton).setOnClickListener(onClickListener);
        findViewById(R.id.passwordButton).setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    //회원가입 완료시 회원가입시 썼던 아이디 가져와서 미리 써두기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                idEditText.setText(data.getStringExtra("id"));
                break;

            default:
                break;

        }

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
                    idEditText.setHint("전화번호 ('-'제외)");
                    break;

                case R.id.registButton:
                    gotoActivity(SignUpActivity.class);
                break;

                case R.id.passwordButton:
                    gotoActivity(FindPwActivity.class);
                    break;

                default:
                    break;
            }
        }
    };

    EditText.OnFocusChangeListener onFocusChangePW = new EditText.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean b) {
            //System.out.println("포커스 준비!");
            user = userDB.isIdExist(idEditText.getText().toString(),
                    ((rGroup.getCheckedRadioButtonId()
                            == R.id.rdoBtnPassenger) ? "St" : "Bd"));
        }
    };

    EditText.OnKeyListener onKeyID = new EditText.OnKeyListener() {

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            //System.out.println("포커스 준비!");
            user = userDB.isIdExist(idEditText.getText().toString(),
                    ((rGroup.getCheckedRadioButtonId()
                    == R.id.rdoBtnPassenger) ? "St" : "Bd"));
            return false;
        }
    };

    private void login() {
        String id = idEditText.getText().toString();
        String pw = pwEditText.getText().toString();

        String type = (rGroup.getCheckedRadioButtonId()
                == R.id.rdoBtnPassenger) ? "St" : "Bd";

        //아이디 및 비밀번호 조건
        if(id.length() < ((type.equals("St")) ? 9 : 10) || pw.length() < 5) {
            startToast(((type.equals("St")) ? "학번은 10자" : "전화번호는 11자")
                        + "리, 비밀번호는 6자리이상입니다. (1)");
            return;
        }

        //길이제한 컷
        id = id.substring(0, ((type.equals("St")) ? 10 : 11));
        user = userDB.isIdExist(id, type);

        //혹시모름
        if(user == null) {
            startToast("다시 시도해주세요 (-1)");
            return;
        }

        //계정없거나 ID 또는 PW 오류
        if(user.id == null || !user.getPW().equals(SunmoonUtil.toSHAString(pw)) || !user.id.equals(id)) {
            startToast(((type.equals("St")) ? "학번" : "전화번호")
                        + " 또는 비밀번호를 다시 확인해주세요 (2)");
            return;
        }

        startToast("로그인에 성공했습니다");

        switch(type){
            case "St"://승객 화면
                gotoActivity(MainActivity.class);
                break;

            case "Bd"://기사 화면
                break;

            default:
                break;
        }
    }

    private void gotoActivity(Class c){
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }

    private void startToast(String msg){
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}