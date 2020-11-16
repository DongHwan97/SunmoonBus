package com.example.sunmoonbus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {
    EditText idEditText, pwEditText;
    RadioGroup rGroup;
    RadioButton rdoPassenger, rdoDriver;
    Button loginBtn;
    Toast toast;

    FirebaseDB2 userDB;
    User user, userAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new FirebaseDB();

        setContentView(R.layout.activity_login);

        userDB = new FirebaseDB2("User");

        idEditText = findViewById(R.id.idEditText);
        pwEditText = findViewById(R.id.pwEditText);
        rGroup = findViewById(R.id.rGroup);
        rdoPassenger = findViewById(R.id.rdoBtnPassenger);
        rdoDriver = findViewById(R.id.rdoBtnDriver);
        loginBtn = findViewById(R.id.loginButton);

        idEditText.setOnFocusChangeListener(onFocusChangePW);
        idEditText.setOnKeyListener(onKeyID);

        loginBtn.setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnDriver).setOnClickListener(onClickListener);
        findViewById(R.id.rdoBtnPassenger).setOnClickListener(onClickListener);

        findViewById(R.id.registButton).setOnClickListener(onClickListener);
        findViewById(R.id.passwordButton).setOnClickListener(onClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if ((userAuto = autoLogin("data.sun")) != null) {
            layoutenable(false);

            user = userDB.isIdExist(userAuto.id, (userAuto.student ? "St" : "Bd"));

            if (userAuto.student == false) {
                rdoDriver.setChecked(true);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                findViewById(R.id.loginlayout).setForeground(ContextCompat.getDrawable(this, R.drawable.foreground_wait));
            } else {
                findViewById(R.id.loginlayout).setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.foreground_wait));
            }

            startToast("잠시만 기다려주세요");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    login(true);
                }
            }, 1200);
        }
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
                pwEditText.setText("");
                break;
            case RESULT_CANCELED:
                finish();
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
                    login(false);
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

    private void login(Boolean autoLogin) {
        String id = idEditText.getText().toString();
        String pw = pwEditText.getText().toString();

        String type = (rGroup.getCheckedRadioButtonId()
                == R.id.rdoBtnPassenger) ? "St" : "Bd";

        //아이디 및 비밀번호 조건
        if(id.length() < ((type.equals("St")) ? 9 : 10) || pw.length() < 5) {
            startToast(((type.equals("St")) ?"학번은 10자" : "전화번호는 11자")
                        + "리, 비밀번호는 6자리이상입니다. (1)");
            return;
        }

        //길이제한 컷
        id = id.substring(0, ((type.equals("St")) ? 10 : 11));
        user = userDB.isIdExist(id, type);

        //혹시모름
        if(user == null) {
            startToast("다시 시도해주세요 (-1)");
            layoutenable(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                findViewById(R.id.loginlayout).setForeground(null);
            } else {
                findViewById(R.id.loginlayout).setBackgroundDrawable(null);
            }
            return;
        }

        if (autoLogin) {
            //자동로그인 - 누군가 비밀번호 변경
            if (!userAuto.getPW().equals(user.getPW())) {
                startToast("회원정보가 변경되었습니다 다시 시도해주세요 (-2)");
                layoutenable(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    findViewById(R.id.loginlayout).setForeground(null);
                } else {
                    findViewById(R.id.loginlayout).setBackgroundDrawable(null);
                }

                return;
            }
        } else {
            //일반 로그인 - 계정없거나 ID 또는 PW 오류
            if(user.id == null || !user.id.equals(id)
                    || !user.getPW().equals(SunmoonUtil.toSHAString(pw))) {
                startToast(((type.equals("St")) ? "학번" : "전화번호")
                        + " 또는 비밀번호를 다시 확인해주세요 (2)");
                return;
            }
        }

        //========================로그인 성공!==========================
        user.student = (type.equals("St")) ? true : false;
        saveAutoLogin("data.sun", user);
        FirebaseDB.user = user;
        startToast("로그인에 성공했습니다");

        gotoActivity(MainActivity.class);
    }

    private User autoLogin(String filename) {
        File file = new File(getFilesDir(), filename);

        if (!file.exists()) {
            try {
                file.createNewFile();
                BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                br.write("#########\n");
                br.write("id=2016\n");
                br.write("password=AAAA\n");
                br.write("type=S\n");
                br.flush(); br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            br.readLine();
            String id = br.readLine().substring(3);
            String pw = br.readLine().substring(9);
            String type = br.readLine().substring(5);
            br.close();

            if (id.equals("2016") || pw.equals("AAAA")) {
                return null;
            }

            if (type.equals("St")) {
                return new User(id, pw, "00000000000", "NONE");
            } else if (type.equals("Bd")) {
                return new User(id, pw);
            } else {
                return null;
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void saveAutoLogin(String filename, User user) {
        File file = new File(getFilesDir(), filename);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            br.write("#########\n");
            br.write("id=" + user.id + "\n");
            br.write("password=" + user.getPW() + "\n");
            br.write("type=" + ((user.student) ? "St" : "Bd") + "\n");
            br.flush(); br.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

    private void layoutenable(Boolean enable) {
        loginBtn.setEnabled(enable);
        idEditText.setEnabled(enable); idEditText.setText(userAuto.id);
        pwEditText.setEnabled(enable);
        rdoPassenger.setEnabled(enable); rdoDriver.setEnabled(enable);
        pwEditText.setText( enable ? "" : userAuto.getPW());
    }
}