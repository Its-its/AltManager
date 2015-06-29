package net.itstjf.accountmanager.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class Encryption {
	private static SecretKeySpec secretKey;
	private static byte[] key;
	
	private static String decryptedString;
	private static String encryptedString;
	
	public static void setKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
            
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String getDecryptedString() {
        return decryptedString;
    }
    
    public static void setDecryptedString(String decryptedString) {
        Encryption.decryptedString = decryptedString;
    }
    
    public static String getEncryptedString() {
        return encryptedString;
    }
    
    public static void setEncryptedString(String encryptedString) {
    	Encryption.encryptedString = encryptedString;
    }
    
	public static String encrypt(String strToEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			setEncryptedString(Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes("UTF-8"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedString;
	}
    
    public static String decrypt(String strToDecrypt) throws Exception {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            setDecryptedString(new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt))));
        return decryptedString;
    }
}