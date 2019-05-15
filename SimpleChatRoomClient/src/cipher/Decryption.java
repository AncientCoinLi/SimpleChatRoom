package cipher;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

public class Decryption {
	/**
     * Do decryption. 
     *
     * @param cipherText
     * @return plainText after decrypt
     */
    public static String AESDecode(String cipherText) {
    	String plainText;
		try {
			Key secretKey = getKey("network");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] c = Base64.getDecoder().decode(cipherText);
			byte[] result = cipher.doFinal(c);
			plainText = new String(result, "UTF-8");
			return plainText;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return cipherText;
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
    
    
	public static boolean shamirValidate(String message, String username, BigInteger n, BigInteger e, BigInteger s, BigInteger t) {
		BigInteger idInt = BigInteger.valueOf(Math.abs(username.hashCode()));
		byte[] msgByte = Util.getUTF8Bytes(message);
		byte[] tByte = t.toByteArray();

		// generate H(t, m)
		byte[] tm = Util.concatenateByteArray(tByte, msgByte);
		byte[] tmHash = CryptoHash.genSha256DigestMulti(tm, 1); // 256 bits = 32 bytes
		BigInteger hash = new BigInteger(tmHash);
		// s^e mod n
		BigInteger v1 = s.modPow(e, n);
		BigInteger v2 = (idInt.multiply(t.modPow(hash, n))).mod(n);
		return v1.equals(v2);
	}
	

	
	public static String RSADecode(String cipherText, BigInteger p, BigInteger q, BigInteger e, BigInteger c) {
		// n = p*q
		BigInteger n = p.multiply(q);
		// ¦µ(n) = (p-1)*(q-1)
		BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		// e*d = 1 (mod(¦µ(n)))
		BigInteger d = e.modInverse(phi);
		// Decryption: m = c^d(mod n)
		BigInteger m = c.modPow(d, n);	
		byte[] m2Byte = m.toByteArray();
		String plainText = new String(m2Byte, StandardCharsets.UTF_8);
		return plainText;
	}
}
