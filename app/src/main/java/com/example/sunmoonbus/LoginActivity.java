package com.example.sunmoonbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class LoginActivity extends AppCompatActivity {
    EditText idEditText, pwEditText;
    RadioGroup rGroup;
    RadioButton rdoPassenger, rdoDriver;
    FirebaseDB2 userDB;
    Boolean once = true;
    User user = new User();
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDB = new FirebaseDB2("User");
        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
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
                case R.id.rdoBtnPassenger:
                    idEditText.setHint("  학번");
                    break;
                case R.id.rdoBtnDriver:
                    idEditText.setHint("  전화번호");
                    break;
            }
        }
    };

    private void login() {
        String id = idEditText.getText().toString();
        String pw = pwEditText.getText().toString();

        if(id.length() < 9 || pw.length() < 5){
            startToast("학번은 10자리이상, 비밀번호는 6자리이상입니다.");
            return;
        }

        String type = (rGroup.getCheckedRadioButtonId() == R.id.rdoBtnPassenger) ? "St" : "Bd";

        user = userDB.isIdExist(id, type);

        //무적권 2번...맘에안듬
        if(once) {
            startToast("다시 시도해주세요 (1)");
            once = false;
            return;
        }

        if(user.id == null || !user.getPW().equals(SunmoonUtil.toSHAString(pw)) || !user.id.equals(id)) {
            startToast("ID 또는 비밀번호를 다시 확인해주세요 (2)");
            //once = true;
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
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }

    private void startToast(String msg){
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}