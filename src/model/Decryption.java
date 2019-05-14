package model;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class Decryption {
	/**
     * Do decryption. 
     *
     * @param cipherText
     * @return plainText after decrypt
     */
    public static String AESDecode(String cipherText) {
    	Key secretKey = getKey("network");
    	try {
    		Cipher cipher = Cipher.getInstance("AES");
    		cipher.init(Cipher.DECRYPT_MODE, secretKey);
    		byte[] c = Base64.getDecoder().decode(cipherText);
    		byte[] result = cipher.doFinal(c);
    		String plainText = new String(result, "UTF-8");
    		return plainText;
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
