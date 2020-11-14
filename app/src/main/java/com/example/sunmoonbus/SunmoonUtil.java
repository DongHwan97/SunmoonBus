package com.example.sunmoonbus;

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
}
