package com.smu.sunmoonbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class FindPwActivity extends AppCompatActivity {
    EditText idEditText, phoneEditText, pwEditText, pwCheckEditText;
    RadioGroup rGroup;
    RadioButton rdoPassenger, rdoDriver;

    AccountDBConnect userDB;
    AccountInfo accountInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        userDB = new AccountDBConnect("User");

        idEditText = findViewById(R.id.idEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        rGroup = findViewById(R.id.rGroup);
        rdoPassenger = findViewById(R.id.rdoBtnPassenger);
        rdoDriver = findViewById(R.id.rdoBtnDriver);

        idEditText.setOnFocusChangeListener(onFocusChangePW);
        idEditText.setOnKeyListener(onKeyID);

        findViewById(R.id.checkBtn).setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnDriver).setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnPassenger).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkBtn:
                    check();
                    break;
                case R.id.reSetBtn:
                    send();
                    break;
                case R.id.rdoBtnPassenger:
                    idEditText.setText(null); idEditText.setHint("학번 또는 교번");
                    idEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
                    phoneEditText.setText(null); phoneEditText.setVisibility(View.VISIBLE);
                    break;

                case R.id.rdoBtnDriver:
                    idEditText.setText(null); idEditText.setHint("전화번호 ('-'제외)");
                    idEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)});
                    phoneEditText.setText(null); phoneEditText.setVisibility(View.GONE);
                    break;
            }
        }
    };

    EditText.OnFocusChangeListener onFocusChangePW = new EditText.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean b) {
            accountInfo = userDB.isIdExist(idEditText.getText().toString(),
                    ((rGroup.getCheckedRadioButtonId()
                            == R.id.rdoBtnPassenger) ? "St" : "Bd"));
        }
    };

    EditText.OnKeyListener onKeyID = new EditText.OnKeyListener() {

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            accountInfo = userDB.isIdExist(idEditText.getText().toString(),
                    ((rGroup.getCheckedRadioButtonId()
                            == R.id.rdoBtnPassenger) ? "St" : "Bd"));
            return false;
        }
    };

    private void check() {
        String type = (rGroup.getCheckedRadioButtonId()
                == R.id.rdoBtnPassenger) ? "St" : "Bd";

        String id = idEditText.getText().toString();
        String ph = type.equals("St") ? phoneEditText.getText().toString() : "none";

        accountInfo = userDB.isIdExist(id, type);

        //회원가입 되지않은 학번
        if(accountInfo == null) {
            SunmoonUtil.startToast(this, "회원가입 되지 않은 사용자입니다.");
            return;
        }

        //회원정보 검증
        if (type.equals("St") ? accountInfo.phoneNumber.equals("none") :
            !accountInfo.phoneNumber.equals("none")) {
            SunmoonUtil.startToast(this, "다시 시도해주세요.");
            return;
        }

        if (!accountInfo.id.equals(id) || (type.equals("St") ? !accountInfo.phoneNumber.equals(ph) : false)) {
            SunmoonUtil.startToast(this, "입력한 정보를 다시 확인해주세요.");
            return;
        }

        //========================비밀번호 초기화 조건 만족!==========================
        SunmoonUtil.startToast(this, "사용자 정보가 확인되었습니다.");
        setContentView(R.layout.activity_reset_pw);

        pwEditText = findViewById(R.id.pwEditText);
        pwCheckEditText = findViewById(R.id.pwEditTextCheck);

        findViewById(R.id.reSetBtn).setOnClickListener(onClickListener);

    }

    private void send() {
        String type = (rGroup.getCheckedRadioButtonId()
                == R.id.rdoBtnPassenger) ? "St" : "Bd";

        String id = idEditText.getText().toString();
        String ph = type.equals("St") ? phoneEditText.getText().toString() : "NONE";

        String pw = (pwEditText.getText().toString()
                .equals(pwCheckEditText.getText().toString()))
                ? pwCheckEditText.getText().toString() : null;

        //비밀번호 비밀번호 확인 같은지?
        if (pw == null) {
            SunmoonUtil.startToast(this, "입력한 정보를 다시 확인해주세요.");
            return;
        }

        //이전에 사용하던 비밀번호로는 사용불가하게
        if (accountInfo.getPW().equals(SunmoonUtil.toSHAString(pw))) {
            SunmoonUtil.startToast(this, "이전에 사용한던 비밀번호로 설정할 수 없습니다.");
            return;
        }

        userDB.upUserInfo(new AccountInfo(id, pw, ph, accountInfo.onBus), type);

        SunmoonUtil.startToast(this, "비밀번호를 변경했습니다.");

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