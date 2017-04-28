import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;

public class Encryptor {
	private static byte iv = 0;
	
	public static void encrypt(byte[] key, String fileFrom, String fileTo, EncryptionModeEnum mode) {
		byte[] content = RW.readB(fileFrom);
		try {
			final SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			byte[] iv = new byte[16];
			Arrays.fill(iv, (byte)0);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
			if (mode == EncryptionModeEnum.OFB) {
				RW.writeB(fileTo, Encryptor.encryptOFB(content, cipher));
			} else if (mode == EncryptionModeEnum.CTR) {
				RW.writeB(fileTo, Encryptor.encryptCTR(content, cipher));
			} else if (mode == EncryptionModeEnum.CBC) {
				RW.writeB(fileTo, Encryptor.encryptCBC(content, cipher));
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] encrypt(byte[] key, byte[] data, EncryptionModeEnum mode) {
		try {
			final SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			byte[] iv = new byte[16];
			Arrays.fill(iv, (byte)0);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
			if (mode == EncryptionModeEnum.OFB) {
				return Encryptor.encryptOFB(data, cipher);
			} else if (mode == EncryptionModeEnum.CTR) {
				return Encryptor.encryptCTR(data, cipher);
			} else if (mode == EncryptionModeEnum.CBC) {
				return Encryptor.encryptCBC(data, cipher);
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	private static byte[] generateIv() {
		byte[] iv = new byte[16];
		Arrays.fill(iv, (byte)0);
		iv[15] = Encryptor.iv;
		Encryptor.iv++;
		return iv;
	}
	
	private static byte[] encrypt(Cipher c, byte[] block) {
		try {
			return c.doFinal(block);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return new byte[16];
	}
	
	private static byte[] encryptOFB(byte[] data, Cipher c) {
		int l = data.length+1;
		byte left = (byte)(16-(l%16));
		int encryptedSize = l + (l%16 == 0 ? 0 : left);
		byte[] encrypted = new byte[encryptedSize+16];
		byte[] iv = Encryptor.generateIv();
		for (int j=0;j<16;j++) encrypted[j] = iv[j];
		
		for (int i=0;i<encryptedSize/16;i++) {
			byte[] block = Arrays.copyOfRange(data, i*16, i*16+16);
			if (i == encryptedSize/16-1) block[15] = left;
			iv = Encryptor.encrypt(c, iv);
			for (int j=0;j<16;j++) encrypted[i*16+j+16] = (byte)(iv[j]^block[j]);
		}
		return encrypted;
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
	
	private static byte[] encryptCTR(byte[] data, Cipher c) {
		int l = data.length+1;
		byte left = (byte)(16-(l%16));
		int encryptedSize = l + (l%16 == 0 ? 0 : left);
		byte[] encrypted = new byte[encryptedSize+16];
		byte[] iv = Encryptor.generateIv();
		for (int j=0;j<16;j++) encrypted[j] = iv[j];
		
		for (int i=0;i<encryptedSize/16;i++) {
			byte[] block = Arrays.copyOfRange(data, i*16, i*16+16);
			if (i == encryptedSize/16-1) block[15] = left;
			byte[] civ = Encryptor.encrypt(c, iv);
			for (int j=0;j<16;j++) encrypted[i*16+j+16] = (byte)(civ[j]^block[j]);
			Encryptor.addOne(iv);
		}
		return encrypted;
	}
	
	private static byte[] encryptCBC(byte[] data, Cipher c) {
		int l = data.length+1;
		byte left = (byte)(16-(l%16));
		int encryptedSize = l + (l%16 == 0 ? 0 : left);
		byte[] encrypted = new byte[encryptedSize+16];
		byte[] iv = Encryptor.generateIv();
		for (int j=0;j<16;j++) encrypted[j] = iv[j];
		
		for (int i=0;i<encryptedSize/16;i++) {
			byte[] block = Arrays.copyOfRange(data, i*16, i*16+16);
			if (i == encryptedSize/16-1) block[15] = left;
			for (int j=0;j<16;j++) iv[j] = (byte)(iv[j]^block[j]);
			iv = Encryptor.encrypt(c, iv);
			for (int j=0;j<16;j++) encrypted[i*16+j+16] = iv[j];
		}
		return encrypted;
	}
}