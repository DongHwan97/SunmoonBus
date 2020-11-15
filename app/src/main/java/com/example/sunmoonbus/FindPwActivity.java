package com.example.sunmoonbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class FindPwActivity extends AppCompatActivity {
    EditText idEditText, phoneEditText, pwEditText, pwCheckEditText;
    RadioGroup rGroup;
    RadioButton rdoPassenger, rdoDriver;

    Toast toast;
    FirebaseDB2 userDB;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        userDB = new FirebaseDB2("User");

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
                    idEditText.setHint("학번");
                    phoneEditText.setVisibility(View.VISIBLE);
                    break;
                case R.id.rdoBtnDriver:
                    idEditText.setHint("전화번호 ('-'제외)");
                    phoneEditText.setVisibility(View.GONE);
                    break;
            }
        }
    };

    EditText.OnFocusChangeListener onFocusChangePW = new EditText.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean b) {
            user = userDB.isIdExist(idEditText.getText().toString(),
                    ((rGroup.getCheckedRadioButtonId()
                            == R.id.rdoBtnPassenger) ? "St" : "Bd"));
        }
    };

    EditText.OnKeyListener onKeyID = new EditText.OnKeyListener() {

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            user = userDB.isIdExist(idEditText.getText().toString(),
                    ((rGroup.getCheckedRadioButtonId()
                            == R.id.rdoBtnPassenger) ? "St" : "Bd"));
            return false;
        }
    };

    private void check() {
        String type = (rGroup.getCheckedRadioButtonId()
                == R.id.rdoBtnPassenger) ? "St" : "Bd";

        String id = idEditText.getText().toString();
        String ph = type.equals("St") ? phoneEditText.getText().toString() : "NONE";

        user = userDB.isIdExist(id, type);

        //혹시모름
        if(user == null || user.phoneNumber == null) {
            startToast("다시 시도해주세요 (-1)");
            return;
        }

        if (type.equals("St") ? user.phoneNumber.equals("NONE") :
            user.phoneNumber != null) {
            startToast("다시 시도해주세요 (-3)");
            return;
        }

        if (!user.id.equals(id) || (type.equals("St") ? !user.phoneNumber.equals(ph) : false)) {
            startToast("입력한 정보를 다시 확인해주세요 (-2)");
            return;
        }

        //========================비밀번호 초기화 조건 만족!==========================
        startToast("사용자 정보가 확인되었습니다.");
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
            startToast("입력한 정보를 다시 확인해주세요 (-3)");
            return;
        }

        //이전에 사용하던 비밀번호로는 사용불가하게
        if (user.getPW().equals(SunmoonUtil.toSHAString(pw))) {
            startToast("이전에 사용한던 비밀번호로 설정할 수 없습니다 (-4)");
            return;
        }

        userDB.upUserInfo(new User(id, pw, ph, user.onBus), type);

        startToast("비밀번호를 변경했습니다");

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

    private void startToast(String msg){
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

}