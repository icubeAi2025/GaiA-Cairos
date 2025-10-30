package kr.co.ideait.platform.gaiacairos.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class AES128Util {
	
	private static volatile AES128Util _INSTANCE;
	
	private final static String _SECRET_KEY = "www.Codefarm.co.kr";
	private static byte[] _IV_128KEY  = new byte[16]; 

	public static AES128Util getInstance() {
		if (_INSTANCE == null) {
			synchronized (AES128Util.class) {
				_INSTANCE = new AES128Util();
			}
		}
		
		return _INSTANCE;
	}
	
	public static String enc(String plainText) {
		String encryptedStr = "";
		
		try {
			System.arraycopy(_SECRET_KEY.getBytes(CharEncoding.UTF_8), 0, _IV_128KEY, 0, 16);
			
			SecretKey secretKey = new SecretKeySpec(_IV_128KEY, "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");			
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(_IV_128KEY));
		
			byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(CharEncoding.UTF_8));			
			encryptedStr = Base64.getUrlEncoder().encodeToString(encryptedBytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			String canonicalName = e.getClass().getCanonicalName();
			if(canonicalName != null){
				log.error("{} Occured", canonicalName.split("[.]")[canonicalName.split("[.]").length-1]);
			}
		}
		
		return encryptedStr;
	}
	
	public static String dec(String cipherText) {
		String decryptedStr = "";
		
		try {
			System.arraycopy(_SECRET_KEY.getBytes(CharEncoding.UTF_8), 0, _IV_128KEY, 0, 16);
			
			SecretKey secretKey = new SecretKeySpec(_IV_128KEY, "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");			
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(_IV_128KEY));
		
			byte[] decryptedBytes = Base64.getUrlDecoder().decode(cipherText.getBytes(Charset.defaultCharset()));
			decryptedStr = new String(cipher.doFinal(decryptedBytes), CharEncoding.UTF_8);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			String canonicalName = e.getClass().getCanonicalName();
			if(canonicalName != null){
				log.error(canonicalName.split("[.]")[canonicalName.split("[.]").length-1] + " Occured");
			}
		}
		
		return decryptedStr;
	}

}