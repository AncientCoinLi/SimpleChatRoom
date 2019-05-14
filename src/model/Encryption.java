package model;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class Encryption {

	public static String encrypt(String plaintext)	{
		return plaintext;
	}
	
	public static String AESEncode(String plainText){
    	Key secretKey = getKey("network");
    	try {
    		Cipher cipher = Cipher.getInstance("AES");
    		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    		byte[] p = plainText.getBytes("UTF-8");
    		byte[] result = cipher.doFinal(p);
    		String encoded = Base64.getEncoder().encodeToString(result);
    		return encoded;
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	} 
	}
	
	private static Key getKey(String keySeed) {  
        if (keySeed == null) {  
            keySeed = System.getenv("AES_SYS_KEY");  
        }  
        if (keySeed == null) {  
            keySeed = System.getProperty("AES_SYS_KEY");  
        }  
        if (keySeed == null || keySeed.trim().length() == 0) {  
            keySeed = "abcd1234!@#$"; 
        }  
        try {  
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");  
            secureRandom.setSeed(keySeed.getBytes());  
            KeyGenerator generator = KeyGenerator.getInstance("AES");  
            generator.init(secureRandom);  
            return generator.generateKey();  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
}
}
