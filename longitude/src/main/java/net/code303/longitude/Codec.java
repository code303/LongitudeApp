package net.code303.longitude;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Codec {

    public static byte[] encr(String cleartext){
        Key key = getKey();
        byte[] ciphertext = new byte[]{};
        try {
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, key);
            ciphertext = aes.doFinal(cleartext.getBytes());
        }
        catch(Exception ex){ex.printStackTrace();}
        return ciphertext;
    }

    public static String decr(byte[] ciphertext) {
        Key key = getKey();
        String cleartext = "";
        try {
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, key);
            cleartext = new String(aes.doFinal(ciphertext));
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return cleartext;
    }

    private static Key getKey() {
        SecretKeySpec key;
        String passphrase = "correct horse battery staple uni";
        try {
            byte[] salt = "choose a better salt".getBytes();
            int iterations = 10000;
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey tmp = factory.generateSecret(new PBEKeySpec(passphrase.toCharArray(), salt, iterations, 128));
            key = new SecretKeySpec(tmp.getEncoded(), "AES");
        }
        catch (Exception ex){ex.printStackTrace();return null;}
        return key;
    }
}
