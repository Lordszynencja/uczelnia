import java.util.*;

public class Challenger {
	public static void challenge(byte[] key, String input0, String input1, String output, EncryptionModeEnum mode) {
		boolean use0 = new Random().nextInt(2) == 0;
		Encryptor.encrypt(key, (use0 ? input0 : input1), output, mode);
	}
	
	public static byte[] challenge(byte[] key, byte[] m0, byte[] m1, EncryptionModeEnum mode, final boolean[] use0) {
		use0[0] = new Random().nextInt(2) == 0;
		return Encryptor.encrypt(key, (use0[0] ? m0 : m1), mode);
	}
}