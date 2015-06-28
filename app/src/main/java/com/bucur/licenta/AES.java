package com.bucur.licenta;


import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by bucur on 6/17/2015.
 */
public class AES {

    static String IV = "AAAAAAAAAAAAAAAA";
    static String encryptionKey = "0123456789abcdef";

    public static String encrypt(String plainText, String encryptionKey) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        String result = android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT);
        Log.d("Timp la criptare:", String.valueOf(plainText.length()) + " " + String.valueOf(encryptedBytes.length) + " "+ String.valueOf(result.length()));
        return result;
    }

    public static String decrypt(String text, String encryptionKey) throws Exception{

        byte[] cipherText = android.util.Base64.decode(text.getBytes("UTF-8"), android.util.Base64.DEFAULT);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
        String result = new String(cipher.doFinal(cipherText));
        Log.d("Timp la decriptare:", String.valueOf(text.length()) + " " + String.valueOf(cipherText.length) + " "+ String.valueOf(result.length()));
        return result;
    }


}
