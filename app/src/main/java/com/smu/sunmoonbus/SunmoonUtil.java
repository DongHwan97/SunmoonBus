package com.smu.sunmoonbus;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SunmoonUtil {

    //PW해시
    public static String toSHAString(String input){
        //System.out.println(new String(new char[5]).replace("\0", "*"));

        StringBuilder sb = null;
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-512");
            sh.update(input.getBytes("UTF-8"));
            byte byteData[] = sh.digest();
            sb = new StringBuilder();
            for(byte b : byteData) {
                String hexString = String.format("%02x", b);
                sb.append(hexString);
            }
            //System.out.println(input + " -> " + sb.toString());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    //NFC태그ID HEX
    public static final String CHARS = "0123456789ABCDEF";
    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; ++i) {
            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(CHARS.charAt(data[i] & 0x0F));
        }
        return sb.toString();
    }

    //인터넷 연결 유무
    public static boolean getNetStatus(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null){
            int type = networkInfo.getType();
            if(type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_WIFI){
                return true;
            }
        }
        startToast(context, "인터넷연결을 확인해주세요");
        return false;  //연결이 되지않은 상태
    }

    //Toast 메시지
    public static Toast toast;
    public static void startToast(Context context, String msg){
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static boolean checkPermission(Activity activity, String[] permission) {
        boolean isGRANTED  = true;
        for (int i = 0; i < permission.length; i++) {
            isGRANTED = isGRANTED && (ActivityCompat.checkSelfPermission(activity, permission[i]) == PackageManager.PERMISSION_GRANTED);
        }

        if (!isGRANTED) {
            ActivityCompat.requestPermissions(activity, permission, 100);
            return false;
        }
        return true;
    }
}
