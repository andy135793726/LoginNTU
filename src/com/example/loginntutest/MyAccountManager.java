package com.example.loginntutest;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.content.SharedPreferences;

public class MyAccountManager {

	private Context context;
	public static final String PREF_NAME = "Account";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	
	private static final String KEY_ENCRYPTION = "85319567";
	
	public enum AccountType
	{
		NTU
	};
	
	
	public MyAccountManager(Context context) {
		this.context = context;
	}
	
	/**
	 * 
	 * 
	 * @param username
	 * @param passwordPlain
	 * @param accountType the target wifi owner, for example, AccountType.NTU 
	 */
	public void saveAccount( String username, String passwordPlain, AccountType accountType ) {
		String passwordCipher = ""; 
		try {
			passwordCipher = encrypt(KEY_ENCRYPTION, passwordPlain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		String keyUsername = accountType.name() + "_" + USERNAME;
		String keyPassword = accountType.name() + "_" + PASSWORD;
		editor.putString(keyUsername, username);
		editor.putString(keyPassword, passwordCipher);
		editor.commit();
	}
	
	public String getUsername(AccountType accountType) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
		return pref.getString(accountType + "_" + USERNAME, "");
	}
	
	public String getPassword(AccountType accountType) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
		String passwordCipher = pref.getString(accountType + "_" + PASSWORD, "");
		String passwordPlain = "";
		try {
			passwordPlain = decrypt(KEY_ENCRYPTION, passwordCipher);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return passwordPlain;
	}
	
	
	private static String encrypt(String seed, String plaintext) throws Exception {
		byte[] rawKey = getRawKey(seed.getBytes()); // may throws exception
		byte[] ciphertext = encrypt(rawKey, plaintext.getBytes());
		return toHex(ciphertext);
	}
	
	private static byte[] encrypt(byte[] key, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");    
        Cipher cipher = Cipher.getInstance("AES");    
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);    
        byte[] ciphertext = cipher.doFinal(plaintext);    
        return ciphertext;
    }    
		
    private static String decrypt(String seed, String ciphertext) throws Exception {    
        byte[] rawKey = getRawKey(seed.getBytes());    
        byte[] cipherByte = toByte(ciphertext);    
        byte[] result = decrypt(rawKey, cipherByte);    
        return new String(result);    
    }    
	
	private static byte[] decrypt(byte[] key, byte[] cyphertext) throws Exception {    
	    SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");    
	    Cipher cipher = Cipher.getInstance("AES");    
	    cipher.init(Cipher.DECRYPT_MODE, skeySpec);    
	    byte[] plaintext = cipher.doFinal(cyphertext);    
	    return plaintext;    
	}    

	
	private static byte[] getRawKey(byte[] seed) throws Exception {    
        KeyGenerator kgen = KeyGenerator.getInstance("AES");    
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");    
        sr.setSeed(seed);    
        kgen.init(128, sr); // 192 and 256 bits may not be available    
        SecretKey skey = kgen.generateKey();    
        byte[] rawKey = skey.getEncoded();    
        return rawKey;    
    }    

	
	private final static String HEX = "0123456789ABCDEF"; 
	private static String toHex(byte[] buf) {
		if (buf == null) return "";
		StringBuffer result = new StringBuffer(2*buf.length);
		for (int i = 0; i < buf.length; i++) {
			result.append(HEX.charAt((buf[i] >> 4) & 0x0f)).append(HEX.charAt(buf[i] & 0x0f));
		}
		return result.toString();
	}
	
	private static byte[] toByte(String hexString) {    
		int len = hexString.length()/2;    
		byte[] result = new byte[len];    
	    for (int i = 0; i < len; i++) {    
	    	result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();    
	    }
	    
	    return result;    
	}   
	
	
}
