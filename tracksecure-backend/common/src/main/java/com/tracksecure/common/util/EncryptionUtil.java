package com.tracksecure.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

//Best practise is to use 16-characters key
public final class EncryptionUtil {
    private EncryptionUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final String ALGORITHM = "AES";
    private static final String CHARSET = "UTF-8";

    // This is a symmetric encryption = same secret key used in both modes
    public static String encrypt(String plainText, String secretKey){
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE,key); // set up for encryption mode
            byte[] encrypted = cipher.doFinal(plainText.getBytes(CHARSET)); // converts the string into bytes
            return Base64.getEncoder().encodeToString(encrypted); //since the data is a binary so we convert it into a Base64 so if we want to send it as a readable string
        }catch(Exception e){
            throw new RuntimeException("Encryption failed",e);
        }
    }

    public static String decrypt(String cipherText, String secretKey){
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,key); // We define the cipher instance on the decrypt mode
            byte[] decoded =  Base64.getDecoder().decode(cipherText);
            return new String(cipher.doFinal(decoded), CHARSET);
        }catch (Exception e){
            throw new RuntimeException("Decryption failed",e);
        }
    }
}
