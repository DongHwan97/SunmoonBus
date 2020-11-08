package com.example.sunmoonbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class SignUpActivity extends AppCompatActivity {
    EditText idEditText, pwEditText, pwCheckEditText;
    RadioGroup rGroup;
    RadioButton rdoPassenger, rdoDriver;
    DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViewById(R.id.signupButton).setOnClickListener(onClickListener);
        idEditText = (EditText)findViewById(R.id.idEditText);
        pwEditText = (EditText)findViewById(R.id.pwEditText);
        pwCheckEditText = (EditText)findViewById(R.id.pwEditTextCheck);
        rGroup = (RadioGroup)findViewById(R.id.rGroup);
        rdoPassenger = (RadioButton)findViewById(R.id.rdoBtnPassenger);
        rdoDriver = (RadioButton)findViewById(R.id.rdoBtnDriver);
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
                    idEditText.setHint("학번");
                    break;
                case R.id.rdoBtnDriver:
                    idEditText.setHint("전화번호");
                    break;
            }
        }
    };


    private void signUp(){
        String id = idEditText.getText().toString();
        String pw = pwEditText.getText().toString();
        String pwCheck = pwCheckEditText.getText().toString();

        if(id.length()>=10&&pw.length()>=6&&pwCheck.length()>=6){
            if(pw.equals(pwCheck)){
                switch(rGroup.getCheckedRadioButtonId()){
                    case R.id.rdoBtnPassenger://승객 기본값 DB에 데이터 저장
                            mDatabase.child("User").child(id).child("password").setValue(pw);
                            mDatabase.child("User").child(id).child("onBus").setValue(false);
                            startToast("회원가입에 성공하셨습니다.");
                            gotoActivity(StartActivity.class);
                            break;
                    case R.id.rdoBtnDriver://기사 DB에 데이터저장
                        mDatabase.child("BusDriver").child(id).setValue(pw);
                        startToast("회원가입에 성공하셨습니다.");
                        gotoActivity(StartActivity.class);
                        break;
                }
            }else{
                startToast("비밀번호가 일치하지 않습니다.");
            }
        }else{
            startToast("학번은 10자리이상, 비밀번호는 6자리이상입니다.");
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