import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class KeyDecryptor {
	private static byte[] keyFromString(String pass) {
		byte[] passBytes = pass.getBytes();
		byte[] key = new byte[16];
		if (passBytes.length < 16) {
			for (int i=0;i<16;i++) key[i] ^= passBytes[i%(passBytes.length)];
		} else {
			for (int i=0;i<passBytes.length;i++) {
				key[i%16] ^= passBytes[i];
			}
		}
		return key;
	}
	
	public static byte[] encryptKeyfile(String content, String password) {
		try {
			final SecretKeySpec skeySpec = new SecretKeySpec(KeyDecryptor.keyFromString(password), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			return cipher.doFinal(content.getBytes());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return content.getBytes();
	}
	
	public static String decryptKeyfile(byte[] content, String password) {
		try {
			final SecretKeySpec skeySpec = new SecretKeySpec(KeyDecryptor.keyFromString(password), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			return new String(cipher.doFinal(content));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return new String(content);
	}
}