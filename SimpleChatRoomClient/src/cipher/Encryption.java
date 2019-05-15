package cipher;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class Encryption {
	private static final int BIT_LENGTH = 512;



	/**
	 * encrypt plain text by AES
	 * @param plainText
	 * @return
	 * @author Brian, Libby
	 */
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
	
	
	public static HashMap<String,String> RSAEncode(String plainText) {
		byte[] msgByte = plainText.getBytes(StandardCharsets.UTF_8);
		BigInteger m = new BigInteger(msgByte); 
		
		// Generate prime p,q randomly
		Random random = new Random();
		BigInteger p = BigInteger.probablePrime(BIT_LENGTH, random);
		BigInteger q = BigInteger.probablePrime(BIT_LENGTH, random);
		
		// n = p*q
		BigInteger n = p.multiply(q);
		
		// ¦µ(n) = (p-1)*(q-1)
		BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		
		// Choose a random integer e
		BigInteger e = BigInteger.probablePrime(16, random);
		e.modInverse(phi);

		// Encryption: c = m^e(mod n)
		BigInteger c = m.modPow(e, n);		
		byte[] cByte = c.toByteArray();
		String cipherText = Base64.getEncoder().encodeToString(cByte);

		HashMap<String, String> rsa = new HashMap<>();
		rsa.put("c", c.toString());
		rsa.put("e", e.toString());
		rsa.put("q", q.toString());
		rsa.put("p", p.toString());
		rsa.put("cipherText", cipherText);
		return rsa;
		
	}

	public static HashMap<String, String> shamirSign(String username, String plainText){
		HashMap<String, String> shamir = new HashMap<>();
		// 32 bytes = 256 bits
		final int length = 32; 
		BigInteger e;
		BigInteger n;
		BigInteger phi;
		byte[] r;
		Random ran = new Random();
		BigInteger p = BigInteger.probablePrime(BIT_LENGTH, ran);
		BigInteger q = BigInteger.probablePrime(BIT_LENGTH, ran);
		n = p.multiply(q);
		phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		e = BigInteger.probablePrime(16, ran);
		
		byte[] msgByte = plainText.getBytes(StandardCharsets.UTF_8);
		BigInteger d = e.modInverse(phi);
		BigInteger idInt = BigInteger.valueOf(Math.abs(username.hashCode()));
		BigInteger sk = idInt.modPow(d, n);
		
		try {
			// choose random r
			CryptoRandomGenerator ranGen = new CryptoRandomGenerator();
			r = ranGen.genCryptoRandom(length);
			BigInteger rInt = new BigInteger(r);
			
			// t = r^e(mod n)
			BigInteger t = rInt.modPow(e, n);
			byte[] tByte = t.toByteArray();
			
			// generate H(t, m)
			byte[] tm = Util.concatenateByteArray(tByte, msgByte);
			byte[] tmHash = CryptoHash.genSha256DigestMulti(tm, 1); // 256 bits = 32 bytes
			BigInteger hash = new BigInteger(tmHash);
			
			// r^hash(mod n)
			BigInteger rr = rInt.modPow(hash, n);
			BigInteger s = (sk.multiply(rr)).mod(n);

			shamir.put("t", t.toString());
			shamir.put("n", n.toString());
			shamir.put("e", e.toString());
			shamir.put("s", s.toString());
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return shamir;
	}
	

	
	
}
