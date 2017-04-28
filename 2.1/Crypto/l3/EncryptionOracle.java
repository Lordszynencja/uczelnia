public class EncryptionOracle {
	public static void encrypt(byte[] key, String[] files, EncryptionModeEnum mode) {
		for (String filename : files) {
			Encryptor.encrypt(key, filename, filename + "out.e", mode);
		}
	}
}