package com.smu.sunmoonbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {
    EditText idEditText, pwEditText, pwCheckEditText;
    EditText phoneEditText;
    RadioGroup rGroup;

    AccountDBConnect userDB;
    AccountInfo accountInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDB = new AccountDBConnect("User");
        setContentView(R.layout.activity_sign_up);

        idEditText = findViewById(R.id.idEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        pwEditText = findViewById(R.id.pwEditText);
        pwCheckEditText = findViewById(R.id.pwEditTextCheck);
        rGroup = findViewById(R.id.rGroup);

        idEditText.setOnFocusChangeListener(onFocusChangePW);
        idEditText.setOnKeyListener(onKeyID);

        findViewById(R.id.signupButton).setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnDriver).setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnPassenger).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.signupButton:
                    signUp();
                    break;
                case R.id.rdoBtnPassenger:
                    idEditText.setText(null); idEditText.setHint("학번 또는 교번");
                    idEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(15)});
                    phoneEditText.setText(null); phoneEditText.setVisibility(View.VISIBLE);
                    pwEditText.setText(null); pwCheckEditText.setText(null);
                    break;

                case R.id.rdoBtnDriver:
                    idEditText.setText(null); idEditText.setHint("전화번호 ('-'제외)");
                    idEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)});
                    phoneEditText.setText(null); phoneEditText.setVisibility(View.GONE);
                    pwEditText.setText(null); pwCheckEditText.setText(null);
                    break;
            }
        }
    };

    EditText.OnFocusChangeListener onFocusChangePW = new EditText.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean b) {
            System.out.println(idEditText.getText().toString());
            accountInfo = userDB.isIdExist(idEditText.getText().toString(),
                    ((rGroup.getCheckedRadioButtonId()
                            == R.id.rdoBtnPassenger) ? "St" : "Bd"));
        }
    };

    EditText.OnKeyListener onKeyID = new EditText.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            String type = ((rGroup.getCheckedRadioButtonId()
                    == R.id.rdoBtnPassenger) ? "St" : "Bd");
            accountInfo = userDB.isIdExist(idEditText.getText().toString(), type);
            return false;
        }
    };

    private void signUp(){
        String id = idEditText.getText().toString();
        String ph = (phoneEditText.getText().toString().length() == 11)
                ? phoneEditText.getText().toString() : "none";
        String pw = (pwEditText.getText().toString()
                .equals(pwCheckEditText.getText().toString()))
                ? pwCheckEditText.getText().toString() : null;

        String type = (rGroup.getCheckedRadioButtonId()
                == R.id.rdoBtnPassenger) ? "St" : "Bd";

        //두번의 비밀번호가 다를때
        if(pw == null) {
            SunmoonUtil.startToast(this, "비밀번호가 다릅니다.");
            return;
        }

        //아이디 및 비밀번호 조건
        if (id.length() < 7 || pw.length() < 5) {
            SunmoonUtil.startToast(this, ((type.equals("St")) ? "아이디는 7자" : "전화번호는 11자")
                        +"리, 비밀번호는 6자리이상입니다.");
            return;
        }

        //학생일때 핸드폰 번호 조건
        if (type.equals("St") ? !(ph.substring(0,3).equals("010")) : false) {
            SunmoonUtil.startToast(this, "정상적인 전화번호가아닙니다.");
            return;
        }

        //길이제한 컷
        //id = id.substring(0, ((type.equals("St")) ? 10 : 11));
        accountInfo = userDB.isIdExist(id, type);

        //정상적인 학번, 전화번호인지 확인
        if (!(type.equals("St")
                ? id.substring(0, 2).equals("20") : id.substring(0, 3).equals("010"))) {
            SunmoonUtil.startToast(this, "정상적인 "
                    + ((type.equals("St")) ? "아이디" : "전화번호가")
                    +"아닙니다.");
            return;
        }

        //혹시모름 + 인터넷 상황이 안좋을때
        if(accountInfo == null) {
            SunmoonUtil.startToast(this, "다시 시도해주세요.");
            return;
        }

        //회원가입 중복방지
        if (!accountInfo.id.equals("none")) {
            SunmoonUtil.startToast(this, "이미 회원가입되어있는 "
                    + "아이디"
                    + "입니다.");
            return;
        }

        //========================회원가입 조건 만족!==========================

        userDB.upUserInfo(new AccountInfo(id, pw, ph, "none"), type);

        SunmoonUtil.startToast(this, "회원가입을 성공했습니다");

        File file = new File(getFilesDir(), "data.sun");
        file.delete();

        try {
            Thread.sleep(500);

            Intent intent = new Intent();
            intent.putExtra("id", id);
            setResult(RESULT_OK, intent);
            finish();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

}