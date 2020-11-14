package com.example.sunmoonbus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SignUpActivity extends AppCompatActivity {
    EditText idEditText, pwEditText, pwCheckEditText;
    RadioGroup rGroup;
    //RadioButton rdoPassenger, rdoDriver;
    Button signupBtn;

    Toast toast;
    FirebaseDB2 userDB;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDB = new FirebaseDB2("User");
        setContentView(R.layout.activity_sign_up);

        idEditText = (EditText)findViewById(R.id.idEditText);
        pwEditText = (EditText)findViewById(R.id.pwEditText);
        pwCheckEditText = (EditText)findViewById(R.id.pwEditTextCheck);
        rGroup = (RadioGroup)findViewById(R.id.rGroup);

        idEditText.setOnFocusChangeListener(onFocusChangePW);
        idEditText.setOnKeyListener(onKeyID);

        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);

        findViewById(R.id.rdoBtnDriver).setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnPassenger).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.loginButton:
                    signUp();
                    break;
                case R.id.rdoBtnPassenger:
                    idEditText.setHint("학번");
                    break;
                case R.id.rdoBtnDriver:
                    idEditText.setHint("전화번호 ('-'제외)");
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

    private void signUp(){
        String id = idEditText.getText().toString();
        String pw = (pwEditText.getText().toString()
                .equals(pwCheckEditText.getText().toString()))
                ? pwCheckEditText.getText().toString() : null;

        String type = (rGroup.getCheckedRadioButtonId()
                == R.id.rdoBtnPassenger) ? "St" : "Bd";

        //두번의 비밀번호가 다를때
        if(pw == null) {
            startToast("비밀번호가 다릅니다. (2)");
            return;
        }

        //아이디 및 비밀번호 조건
        if (id.length() < 9 || pw.length() < 5) {
            startToast(((type.equals("St")) ? "학번은 10자" : "전화번호는 11자")
                        +"리, 비밀번호는 6자리이상입니다. (1)");
            return;
        }

        //길이제한 컷
        id = id.substring(0, ((type.equals("St")) ? 10 : 11));
        user = userDB.isIdExist(id, type);

        //정상적인 학번, 전화번호인지 확인
        if (!(type.equals("St")
                ? id.substring(0, 2).equals("20") : id.substring(0, 3).equals("010"))) {
            startToast("정상적인 "
                    + ((type.equals("St")) ? "학번이" : "전화번호가")
                    +"아닙니다. (3)");
            return;
        }

        //혹시모름
        if(user == null) {
            startToast("다시 시도해주세요 (-1)");
            return;
        }

        //회원가입 중복방지
        if (!user.id.equals("NONE")) {
            startToast("이미 회원가입되어있는 "
                    + ((type.equals("St")) ? "학번" : "아이디")
                    + "입니다. (-2)");
            return;
        }

        userDB.upUserInfo(new User(id, pw), type);

        startToast("회원가입을 성공했습니다");

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