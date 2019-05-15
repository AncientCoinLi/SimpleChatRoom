package cipher;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.utils.Utils;

public class AESEncryption {
	
	private byte[] enc;
	private String secretTrigger;
	private String ivTrigger;
	
	public AESEncryption(String secretTrigger, String ivTrigger) {
		this.secretTrigger = secretTrigger;
		this.ivTrigger = ivTrigger;
	}

	public byte[] encAes256CbcByte(String msg) throws Exception {
		final SecretKeySpec key = new SecretKeySpec(getUTF8Bytes(secretTrigger), "AES");
		final IvParameterSpec iv = new IvParameterSpec(getUTF8Bytes(ivTrigger));		
		
		Properties properties = new Properties();
        //Creates a CryptoCipher instance with the transformation and properties.
        final String transform = "AES/CBC/PKCS5Padding";
        final ByteBuffer outBuffer;
        final int bufferSize = 1024;
        final int updateBytes;
        final int finalBytes;
        try (CryptoCipher encipher = Utils.getCipherInstance(transform, properties)) {
        	System.out.println("transform|" + transform + "|");
        	
            ByteBuffer inBuffer = ByteBuffer.allocateDirect(bufferSize);
            outBuffer = ByteBuffer.allocateDirect(bufferSize);
            inBuffer.put(getUTF8Bytes(msg));

            inBuffer.flip(); // ready for the cipher to read it
            // Initializes the cipher with ENCRYPT_MODE,key and iv.
            encipher.init(Cipher.ENCRYPT_MODE, key, iv);
            // Continues a multiple-part encryption/decryption operation for byte buffer.
            updateBytes = encipher.update(inBuffer, outBuffer);

            // We should call do final at the end of encryption/decryption.
            finalBytes = encipher.doFinal(inBuffer, outBuffer);
        }

        outBuffer.flip(); // ready for use as decrypt
        enc = new byte[updateBytes + finalBytes];
        outBuffer.duplicate().get(enc);
        //System.out.println(Arrays.toString(encR1));       
        return enc;
    }
	

	/**
     * Converts String to UTF8 bytes
     *
     * @param input the input string
     * @return UTF8 bytes
     */
    public static byte[] getUTF8Bytes(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Converts ByteBuffer to String
     * 
     * @param buffer input byte buffer
     * @return the converted string
     */
    private static String asString(ByteBuffer buffer) {
        final ByteBuffer copy = buffer.duplicate();
        final byte[] bytes = new byte[copy.remaining()];
        copy.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

}