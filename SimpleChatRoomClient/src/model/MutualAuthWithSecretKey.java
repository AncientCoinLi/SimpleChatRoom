package model;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import org.apache.commons.crypto.random.CryptoRandom;
import org.apache.commons.crypto.random.CryptoRandomFactory;

import cipher.AESEncryption;




public class MutualAuthWithSecretKey {
	
	final static String HASH_SHA3_256 = "SHA3-256";
	final static String HASH_SHA_256 = "SHA-256";

    public static void main(String []args) throws Exception {  	
    	
    	// Generate shared secret K
    	String secretTrigger = "s3647369s3647369s3647369s3647369";
    	String ivTrigger = "s3647369s3647369";
    	String id = "s3647369";
    	byte[] idByte = {'s','3','6','4','7','3','6','9'};
    	String idInt = "8301039218330776369";
    	System.out.println("Alice's id:"+id);
    	System.out.println("Alice's id byte:"+Arrays.toString(idByte));
    	System.out.println("Alice's id int:"+idInt);
    	
    	String msg = "Cloud Security";
    	byte[] msgByte = {'C','l','o','u','d',' ','s','e','c','u','r','i','t','y'};
    	String msgInt = "1367512579749448790337776792466553";
    	System.out.println("Alice's message:"+msg);
    	System.out.println("Alice's message byte:"+Arrays.toString(msgByte));
    	System.out.println("Alice's message int:"+msgInt);
    	
    	BigInteger n = new BigInteger("102366816261339502408990957400620279456327864403206489995593575913852258538711356969108675703470979032234612014212684280721266556973494343164060941804306335120329921810528784616631021503798213321045999086080057796902902549800666717342710060328133457333802321622599745199280804320741074960463599034018899682873");
    	BigInteger d = new BigInteger("15594706708473283672602739195992994340479078966104850635763099652469611810892994613418084718913808301536999959563291573595390776276353319836885796465724617350043083902386151271549820824834006106638865713316423559144184135073510760849159447895254775932125333633695859859291001670588456398433933585857720921089");
    	
    	
    	System.out.println(d);
    	
//    	System.out.println("[Shared password]:" + secretTrigger);
//    	System.out.println("[Alice side]: Alice sends id to Bob.");
    	
    	// Generate challenges R1 and R2
    	int length = 32;
    	byte[] r1 = MutualAuthWithSecretKey.genCryptoRandom(length);      	
    	BigInteger rInt = new BigInteger(r1);
    	
    	
    	String r1Base64 = Base64.getEncoder().encodeToString(r1); 
    	System.out.println("[Bob side]: Bob generates challenge R1 randomly. R1:" + r1Base64);
    	
    	byte[] r2 = MutualAuthWithSecretKey.genCryptoRandom(length);
    	String r2Base64 = Base64.getEncoder().encodeToString(r2);
    	System.out.println("[Alice side]: Alice generates challenge R2 randomly. R2:" + r2Base64);
    	
    	// Encrypt challenges R1 and R2
    	AESEncryption aesEnc = new AESEncryption(secretTrigger,ivTrigger);
    	byte[] r1Cipher = aesEnc.encAes256CbcByte(r1Base64);
    	String r1CipherBase64 = Base64.getEncoder().encodeToString(r1Cipher);
    	System.out.println("[Alice side]: Alice sends encrypted Enc(R1) and R2 to Bob: " + r1CipherBase64);	
    	
    	System.out.println("[Bob side]: Bob decrypts and gets R1 to verify the correctness.");
    	
    	byte[] r2Cipher = aesEnc.encAes256CbcByte(r2Base64);
    	String r2CipherBase64 = Base64.getEncoder().encodeToString(r2Cipher);
    	System.out.println("[Bob side]: Bob sends encrypted Enc(R2) and R1 to Alice: " + r2CipherBase64);
    	
    	System.out.println("[Alice side]: Alice decrypts and gets R2 to verify the correctness.");
    }
    
    
    
    
    public static byte[] genCryptoRandom(int length) throws IOException, GeneralSecurityException {
        // Constructs a byte array to store random data.
        byte[] r = new byte[length];  
        
        Properties properties = new Properties();              
        properties.put(CryptoRandomFactory.CLASSES_KEY.getBytes(),CryptoRandomFactory.RandomProvider.OPENSSL.getClassName().getBytes());
            
        // Gets the 'CryptoRandom' instance.
        try (CryptoRandom random = CryptoRandomFactory.getCryptoRandom(properties)){       	
            // Show the actual class (may be different from the one requested)
//          System.out.println(random.getClass().getCanonicalName());

            // Generate random bytes and places them into the byte arrays.
            random.nextBytes(r);
      
            return r;
        }
    }
    
    public static byte[] genSha256DigestMulti(String msg, int round) {
    	return genSha256DigestMulti(getUTF8Bytes(msg),round);
    }
    
    public static byte[] genSha256DigestMulti(byte[] msg, int round) {
    	MessageDigest sha256;
    	byte[] hash256 = new byte[32];
    	try {
    		sha256 = MessageDigest.getInstance(HASH_SHA_256);
    		for(int i=0; i<round-1; i++) {
    			sha256.update(msg);
    		}
    		
    		hash256 = sha256.digest();
    		
    	}catch(Exception e) {
    		System.err.println("ERROR || SHA-256 NoSuchAlgorithmExpection.");
    		e.printStackTrace();
    	}
    	
    	return hash256;
    }
    
    
//	public int exponentiation(a,m,n){
//		// convert e to binary
//		a = a.toString(2); 
//
//		String power = "";
//		int c = 1;
//
//		for(var i=a.length-1; i>=0; i--)
//		{
//			power += a.charAt(i);
//		}
//		// fast exponentiation
//		if(power.charAt(0)=='1')
//			c = c * (m % n);
//
//		for(var j=1; j<power.length; j++)
//		{
//			if(power.charAt(j)=='1')
//			{
//				var count = j, temp = m;
//				while(count>0)
//				{
//					temp = Math.pow(temp,2) % n;
//					count--;
//				}
//				c = (c * temp) % n;
//			}								
//		}
//		return c;
//	}
    
    
	/**
     * Converts String to UTF8 bytes
     *
     * @param input the input string
     * @return UTF8 bytes
     */   
    private static byte[] getUTF8Bytes(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }
    

    
    
}