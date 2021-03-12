package com.elan.p2pchat.Utils;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static AES aes;
    private static final String ALGO = "AES";
    private byte[] keyValue;
    private static final String key="lv39eptlvuhaqqqw";

    // singleton object
    private AES() {
        keyValue = key.getBytes();
    }

    public static AES getInstance()
    {
        if(aes==null)
            aes=new AES();
        return aes;
    }

    //encrypting message
    public String encrypt(String data) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = cipher.doFinal(data.getBytes());
        String encryptedValue = Base64.encodeToString(encValue, 0);
        return encryptedValue;

    }

    //decrypting message
    public String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.decode(encryptedData, 0);
        byte[] decValue = cipher.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private Key generateKey() {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }
}
