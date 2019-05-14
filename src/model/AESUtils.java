
package model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import java.util.Base64;


public class AESUtils {
 
	
    private static final String encodeRules = "cloud";
 
    /**
     * Encrypt content.
     * 1.Make a key constructor
     * 2.Initiate a key generator base on encodeRules
     * 3.Generate the key
     * 4.Do encryption of the content
     * 5.return String of cipherText
     */
    public static String AESEncode(String content) {
        try {
            // 1.Make a key constructor
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            // 2.Initiate a key generator base on encodeRules
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(encodeRules.getBytes());
            keygen.init(128, random);            
            SecretKey original_key = keygen.generateKey();
            byte[] raw = original_key.getEncoded();
            // 3.Generate the key
            SecretKey key = new SecretKeySpec(raw, "AES");           
            Cipher cipher = Cipher.getInstance("AES");
            // 4.Do encryption of the content
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] byte_encode = content.getBytes("utf-8");
            byte[] byte_AES = cipher.doFinal(byte_encode);
            String AES_encode = new String(Base64.getEncoder().encodeToString(byte_AES));
            // 5.return String of cipherText
            return AES_encode;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
 
    
 
    /** 
     * AES encryption
     * @param content prepare to encrypt 
     * @param encryptKey key of encryption 
     * @return the encrypted array of byte[]
     * @throws Exception 
     */  
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
        kgen.init(128, new SecureRandom(encryptKey.getBytes()));  
  
        Cipher cipher = Cipher.getInstance("AES");  
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));  
          
        return cipher.doFinal(content.getBytes("utf-8"));  
    }  
    
    /**
     * Decryption
     * The process of decryption
     * 1.reverse of 4 steps which are similar to encryption
     * 2.change the string into byte[]
     * 3.decrypt the cipher text
     */
    public static String AESDecode(String content) {
        try {
            //1.construct the key generator
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            //2.Based on the ecnodeRules to initialize the key generator
            //randomly generate 128 bytes
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(encodeRules.getBytes());
            keygen.init(128, random);
            //3.generate original symmetry key
            SecretKey original_key = keygen.generateKey();
            //4.acquire the original symmetry key
            byte[] raw = original_key.getEncoded();
            //5.According to byte array to generate AES key
            SecretKey key = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            //6.initialize key generator��the first parameter is (Encrypt_mode) or (Decrypt_mode)��the second parameter is KEY
            cipher.init(Cipher.DECRYPT_MODE, key);
            //7.decrypt the data from byte array
            //byte[] byte_content = new BASE64Decoder().decodeBuffer(content);
            byte[] byte_content = Base64.getDecoder().decode(content);
            /*
             * Decryption
             */
            byte[] byte_decode = cipher.doFinal(byte_content);
            String AES_decode = new String(byte_decode, "utf-8");
            return AES_decode;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        //if there is a mistake, the return null
        return null;
    }
    
    
    /** 
     * AES decryption 
     * @param encryptBytes the byte[] of preparing to be decrypted 
     * @param decryptKey decrypt the AES key 
     * @return the String after decryption
     * @throws Exception 
     */  
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
        kgen.init(128, new SecureRandom(decryptKey.getBytes()));  
          
        Cipher cipher = Cipher.getInstance("AES");  
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));  
        byte[] decryptBytes = cipher.doFinal(encryptBytes);  
          
        return new String(decryptBytes);  
    }  
    
    /** 
     * base 64 encryption 
     * @param bytes preparing to byte[] 
     * @return After base 64 code 
     */  
    public static String base64Encode(byte[] bytes){  
        return Base64.getEncoder().encodeToString(bytes);  
    }  
    
    /** 
     * base 64 decryption 
     * @param base64Code preparing to base 64 code 
     * @return the byte[] after decryption
     * @throws Exception 
     */  
    public static byte[] base64Decode(String base64Code) throws Exception{  
     
        if(base64Code.isEmpty()) {
        	return null;
        } else {
        	return Base64.getDecoder().decode(base64Code);
        }
        
    }  
    
    /** 
     * To encrypt the base 64 code AES 
     * @param encryptStr base 64 code is preparing to decrypt
     * @param decryptKey key of decryption
     * @return string after decryption
     * @throws Exception 
     */  
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {  
        
        if(encryptStr.isEmpty()) {
        	return null;
        } else {
        	return aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
        }
    }  
    
    /** 
     * Invoking the AES to AESbase 64 code 
     * @param content the content is preparing to encrypt 
     * @param encryptKey the key of encryption 
     * @return base 64 code after encryption
     * @throws Exception 
     */  
    public static String aesEncrypt(String content, String encryptKey) throws Exception {  
        return base64Encode(aesEncryptToBytes(content, encryptKey));  
    }
 
    public static void main(String[] args) {
        String[] keys = {
                "", "root"
        };
        System.out.println("key | AESEncode | AESDecode");
        for (String key : keys) {
            System.out.print("key:"+key + " | ");
            String encryptString = AESEncode(key);
            System.out.print(encryptString + " | ");
            String decryptString = AESDecode(encryptString);
            System.out.println(decryptString);
        }
    }

}
