/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.security;

import java.security.Key;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * @author abhishekagarwal
 * This utility provides us with functionality of encrypting and decrypting account number based
 * on a pre specified key
 */
@Named
public class EncryptDecryptUtil {

	private static final String ALGORITHM = "AES";
	private static final String keyValue = "AB020989RA070190";
	private Key key;
	
	@PostConstruct
	public void start(){
		key = new SecretKeySpec(keyValue.getBytes(), ALGORITHM);
	}

	 public String encrypt(String valueToEnc) throws Exception {
	    Cipher c = Cipher.getInstance(ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key);
	    byte[] encValue = c.doFinal(valueToEnc.getBytes());
	    String encryptedValue = Base64.encodeBase64String(encValue);
	    return "AF"+encryptedValue;
	}

	public String decrypt(String encryptedValue) throws Exception {
		encryptedValue = encryptedValue.substring(2);
	    Cipher c = Cipher.getInstance(ALGORITHM);
	    c.init(Cipher.DECRYPT_MODE, key);
	    byte[] decordedValue = Base64.decodeBase64(encryptedValue);
	    byte[] decValue = c.doFinal(decordedValue);
	    String decryptedValue = new String(decValue);
	    return decryptedValue;
	}
	
	public String decryptAndMask(String encryptedValue) throws Exception {
		if (StringUtils.isBlank(encryptedValue)){
			return null;
		}
	    String decryptedValue = decrypt(encryptedValue);
	    return maskAccountNumber(decryptedValue);
	}
	
	public String maskAccountNumber(String account){
		return mask(account, '*', 4);
	}
	
	public String mask(String account, char c, int unmasklength) {
		int len = account.length();
		if (len > unmasklength){
			StringBuilder sb = new StringBuilder();
			int i = 1;
			for(; i<= len-unmasklength; i++){
				sb.append(c);
			}
			for (;i<=len;i++){
				sb.append(account.charAt(i-1));
			}
			account = sb.toString();
		}
		return account;
	}

}
