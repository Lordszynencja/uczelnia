import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;

public class Decryptor {
	public static void decrypt(byte[] key, String fileFrom, String fileTo, EncryptionModeEnum mode) {
		RW.writeB(fileTo, Decryptor.decrypt(key, RW.readB(fileFrom), mode));
	}
	
	public static byte[] decrypt(byte[] key, byte[] data, EncryptionModeEnum mode) {
		try {
			final SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			byte[] iv = new byte[16];
			Arrays.fill(iv, (byte)0);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			if (mode == EncryptionModeEnum.OFB) {
				cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
				return Decryptor.decryptOFB(data, cipher);
			} else if (mode == EncryptionModeEnum.CTR) {
				cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
				return Decryptor.decryptCTR(data, cipher);
			} else if (mode == EncryptionModeEnum.CBC) {
				cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);
				return Decryptor.decryptCBC(data, cipher);
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	private static byte[] decrypt(Cipher c, byte[] block) {
		try {
			return c.doFinal(block);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return new byte[16];
	}
	
	private static byte[] decryptOFB(byte[] data, Cipher c) {
		byte[] decrypted = new byte[data.length-16];
		byte[] iv = Arrays.copyOfRange(data, 0, 16);
			
		for (int i=0;i<data.length/16-1;i++) {
			byte[] block = Arrays.copyOfRange(data, i*16+16, i*16+32);
			iv = Decryptor.decrypt(c, iv);
			for (int j=0;j<16;j++) decrypted[i*16+j] = (byte)(iv[j]^block[j]);
		}
		byte left = decrypted[decrypted.length-1];
		return Arrays.copyOf(decrypted, decrypted.length-left-1);
	}
	
	private static void addOne(byte[] ctr) {
		if (ctr.length != 16) return;
		ctr[15] += 1;
		
		int i=15;
		while (i>0 && ctr[i] == 0) {
			ctr[i-1] += 1;
			i--;
		}
	}
	
	private static byte[] decryptCTR(byte[] data, Cipher c) {
		byte[] decrypted = new byte[data.length-16];
		byte[] iv = Arrays.copyOfRange(data, 0, 16);
			
		for (int i=0;i<data.length/16-1;i++) {
			byte[] block = Arrays.copyOfRange(data, i*16+16, i*16+32);
			byte[] civ = Decryptor.decrypt(c, iv);
			for (int j=0;j<16;j++) decrypted[i*16+j] = (byte)(civ[j]^block[j]);
			Decryptor.addOne(iv);
		}
		byte left = decrypted[decrypted.length-1];
		return Arrays.copyOf(decrypted, decrypted.length-left-1);
	}
	
	private static byte[] decryptCBC(byte[] data, Cipher c) {
		byte[] decrypted = new byte[data.length-16];
		for (int i=0;i<data.length/16-1;i++) {
			byte[] iv = Arrays.copyOfRange(data, i*16, i*16+16);
			byte[] block = Arrays.copyOfRange(data, i*16+16, i*16+32);
			block = Decryptor.decrypt(c, block);
			for (int j=0;j<16;j++) decrypted[i*16+j] = (byte)(iv[j]^block[j]);
		}
		byte left = decrypted[decrypted.length-1];
		return Arrays.copyOf(decrypted, decrypted.length-left-1);
	}
}