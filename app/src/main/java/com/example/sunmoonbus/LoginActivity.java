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

    AccountDBConnect userDB;
    AccountInfo accountInfo, accountInfoAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ShuttleDBConnect();

        setContentView(R.layout.activity_login);

        userDB = new AccountDBConnect("User");

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

        if ((accountInfoAuto = autoLogin()) != null) {
            layoutenable(false);

            accountInfo = userDB.isIdExist(accountInfoAuto.id, (accountInfoAuto.student ? "St" : "Bd"));

            if (accountInfoAuto.student == false) {
                rdoDriver.setChecked(true);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                findViewById(R.id.loginlayout).setForeground(ContextCompat.getDrawable(this, R.drawable.foreground_wait));
            } else {
                findViewById(R.id.loginlayout).setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.foreground_wait));
            }

            SunmoonUtil.startToast(this, "잠시만 기다려주세요");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!SunmoonUtil.getNetStatus(LoginActivity.this)) {
                        layoutenable(true);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            findViewById(R.id.loginlayout).setForeground(null);
                        } else {
                            findViewById(R.id.loginlayout).setBackgroundDrawable(null);
                        }
                        return;
                    }
                    login(true);
                }
            }, 1300);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    //다음화면에서 로그인화면으로 돌아왔을때
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 0://MainActivity
                if (resultCode == RESULT_CANCELED) {
                    finish();
                    break;
                }
                File file = new File(getFilesDir(), "data.sun");
                file.delete();
                ShuttleDBConnect.accountInfo = null;
                layoutenable(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    findViewById(R.id.loginlayout).setForeground(null);
                } else {
                    findViewById(R.id.loginlayout).setBackgroundDrawable(null);
                }
                break;

            case 1://SignUpActivity
                break;

            case 2://FindPwActivity
                break;

            default:
                break;
        }

        switch (resultCode) {
            case RESULT_OK:
                idEditText.setText(data.getStringExtra("id"));
                pwEditText.setText("");
                break;

            default:
                break;

        }

    }

    View.OnClickListener onClickListener=new View.OnClickListener(){

        @Override
        public void onClick(View view) {

            if (!SunmoonUtil.getNetStatus(LoginActivity.this)) {
                layoutenable(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    findViewById(R.id.loginlayout).setForeground(null);
                } else {
                    findViewById(R.id.loginlayout).setBackgroundDrawable(null);
                }
                return;
            }

            switch (view.getId()){
                case R.id.loginButton:
                    login(false);
                    break;

                case R.id.rdoBtnPassenger:
                    idEditText.setText(null); pwEditText.setText(null);
                    idEditText.setHint("학번");
                    break;

                case R.id.rdoBtnDriver:
                    idEditText.setText(null); pwEditText.setText(null);
                    idEditText.setHint("전화번호 ('-'제외)");
                    break;

                case R.id.registButton:
                    startActivityForResult(new Intent(LoginActivity.this, SignUpActivity.class), 1);
                break;

                case R.id.passwordButton:
                    startActivityForResult(new Intent(LoginActivity.this, FindPwActivity.class), 2);
                    break;

                default:
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

    private void login(Boolean autoLogin) {

        String id = idEditText.getText().toString();
        String pw = pwEditText.getText().toString();

        String type = (rGroup.getCheckedRadioButtonId()
                == R.id.rdoBtnPassenger) ? "St" : "Bd";

        //아이디 및 비밀번호 조건
        if(id.length() < ((type.equals("St")) ? 9 : 10) || pw.length() < 5) {
            SunmoonUtil.startToast(this, ((type.equals("St")) ?"학번은 10자" : "전화번호는 11자")
                        + "리, 비밀번호는 6자리이상입니다.");
            return;
        }

        //길이제한 컷
        id = id.substring(0, ((type.equals("St")) ? 10 : 11));
        accountInfo = userDB.isIdExist(id, type);

        //혹시모름 + 인터넷 상황이 안좋을때
        if(accountInfo == null) {
            layoutenable(true);
            SunmoonUtil.startToast(this, "인터넷 연결이 불안정합니다 다시 시도해주세요.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                findViewById(R.id.loginlayout).setForeground(null);
            } else {
                findViewById(R.id.loginlayout).setBackgroundDrawable(null);
            }
            return;
        }

        if (autoLogin) {
            //자동로그인 - 누군가 비밀번호 변경
            if (!accountInfoAuto.getPW().equals(accountInfo.getPW())) {
                SunmoonUtil.startToast(this, "회원정보가 변경되었습니다 다시 시도해주세요.");
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
            if(accountInfo.id == null || !accountInfo.id.equals(id)
                    || !accountInfo.getPW().equals(SunmoonUtil.toSHAString(pw))) {
                SunmoonUtil.startToast(this, ((type.equals("St")) ? "학번" : "전화번호")
                        + " 또는 비밀번호를 다시 확인해주세요.");
                return;
            }
        }

        //========================로그인 성공!==========================
        accountInfo.student = (type.equals("St")) ? true : false;
        saveAutoLogin(accountInfo);
        ShuttleDBConnect.accountInfo = accountInfo;
        SunmoonUtil.startToast(this, "로그인에 성공했습니다");

        startActivityForResult(new Intent(this, MainActivity.class), 0);
    }

    private AccountInfo autoLogin() {
        File file = new File(getFilesDir(), "data.sun");

        if (!file.exists()) {
            try {
                file.createNewFile();
                BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                br.write("#########\n");
                br.write("id=2016\n");
                br.write("password=AAAA\n");
                br.write("type=S\n");
                br.flush(); br.close();
                return null;

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

            if (type.equals("St")) {
                return new AccountInfo(id, pw, "00000000000", "none");
            } else if (type.equals("Bd")) {
                return new AccountInfo(id, pw);
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void saveAutoLogin(AccountInfo accountInfo) {
        File file = new File(getFilesDir(), "data.sun");

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
            br.write("id=" + accountInfo.id + "\n");
            br.write("password=" + accountInfo.getPW() + "\n");
            br.write("type=" + ((accountInfo.student) ? "St" : "Bd") + "\n");
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

    private void layoutenable(Boolean enable) {
        loginBtn.setEnabled(enable);
        idEditText.setEnabled(enable); idEditText.setText(accountInfoAuto.id);
        pwEditText.setEnabled(enable);
        rdoPassenger.setEnabled(enable); rdoDriver.setEnabled(enable);
        pwEditText.setText( enable ? "" : accountInfoAuto.getPW());

    }
}