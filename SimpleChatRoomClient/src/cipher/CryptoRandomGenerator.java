package cipher;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.crypto.random.CryptoRandom;
import org.apache.commons.crypto.random.CryptoRandomFactory;


public class CryptoRandomGenerator {
	
	
	public byte[] genCryptoRandom(int length) throws GeneralSecurityException, IOException{
		byte[] r = new byte[length];
		Properties properties = new Properties();
		// comment out the following line if error occurs
		//properties.put(CryptoRandomFactory.CLASSES_KEY, CryptoRandomFactory.RandomProvider.OPENSSL.getClassName());
		
		try (CryptoRandom random = CryptoRandomFactory.getCryptoRandom(properties)) {
						
			// Show the actual class (may be different from the one requested)
			System.out.println(random.getClass().getCanonicalName());			
			
			random.nextBytes(r);
			
			return r;
		}
		
		
	}
	
	public byte[] genCryptoRanOdd(int length) throws GeneralSecurityException, IOException {
		CryptoRandomGenerator gen = new CryptoRandomGenerator();
		byte[] rByte = gen.genCryptoRandom(length-1);
		byte one = 1;
		byte last = rByte[length-2];
		byte a = (byte) (one + last * 2);
		byte[] rOdd = new byte[length];
		rOdd = Arrays.copyOf(rByte, length);
		rOdd[length-1]=a; 
		
		return rOdd;
	}
	
	public byte[] genCryptoRanEven(int length) throws GeneralSecurityException, IOException {
		CryptoRandomGenerator gen = new CryptoRandomGenerator();
		byte[] rByte = gen.genCryptoRandom(length-1);
		//byte one = 1;
		byte last = rByte[length-2];
		byte a = (byte) (last * 2);
		byte[] rEven = new byte[length];
		rEven = Arrays.copyOf(rByte, length);
		rEven[length-1]=a; 
		
		return rEven;
	}
	
	
//	public static void main(String[] args) throws GeneralSecurityException, IOException {
//		// Constructs a byte array to store random data.
//		byte[] key = new byte[16];
//		byte[] iv = new byte[32];
//
//		Properties properties = new Properties();
//		//properties.put(CryptoRandomFactory.CLASSES_KEY, CryptoRandomFactory.RandomProvider.OPENSSL.getClassName());
//
//		// Gets the 'CryptoRandom' instance.
//		try (CryptoRandom random = CryptoRandomFactory.getCryptoRandom(properties)) {
//
//			// Show the actual class (may be different from the one requested)
//			System.out.println(random.getClass().getCanonicalName());
//
//			// Generate random bytes and places them into the byte arrays.
//			random.nextBytes(key);
//			random.nextBytes(iv);
//			
//			
//
//		}
//
//		// Show the generated output
//		System.out.println(Arrays.toString(key));
//		System.out.println(Arrays.toString(iv));
//		
//		CryptoRandomGenerator gen = new CryptoRandomGenerator();
//		byte[] rOdd = gen.genCryptoRanOdd(16);
//		System.out.println(Arrays.toString(rOdd));
//		byte[] rEven = gen.genCryptoRanEven(16);
//		System.out.println(Arrays.toString(rEven));
		
//	}
}