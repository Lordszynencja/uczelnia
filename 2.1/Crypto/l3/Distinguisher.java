import java.util.*;

public class Distinguisher {
	private static byte[] addOne(byte[] m) {
		byte[] copy = Arrays.copyOf(m, m.length);
		
		int i = copy.length-1;
		copy[i] += 1;
		while (i>0 && copy[i] == 0) {
			copy[i-1] += 1;
			i--;
		}
		return copy;
	}
	
	private static byte[] xor(byte[] m0, byte[] m1) {
		if (m0.length < m1.length) return Distinguisher.xor(m1, m0);
		byte[] m = Arrays.copyOf(m0, m0.length);
		for (int i=0;i<m1.length;i++) {
			m[m.length-1-i] ^= m1[m1.length-i-1];
		}
		return m;
	}
	
	public static boolean distinguish() {
		byte[] m0 = "1234567890123456789012345678901".getBytes();
		byte[] m1 = "1234567890123456789012345678900".getBytes();
		byte[] key = Z1.createKey();
		boolean[] use0 = new boolean[1];
		byte[] c = Challenger.challenge(key, m0, m1, EncryptionModeEnum.CBC, use0);
		
		byte[] iv = Arrays.copyOfRange(c, 0, 16);
		byte[] ivPP = Distinguisher.addOne(iv);
		byte[] mStart = Distinguisher.xor(Distinguisher.xor(Arrays.copyOf(m0, 16), iv), ivPP);
		byte[] m = Arrays.copyOf(m0, m0.length);
		for (int i=0;i<16;i++) m[i] = mStart[i];
		byte[] c1 = Encryptor.encrypt(key, m, EncryptionModeEnum.CBC);
		
		if (c1.length != c.length) return false;
		else {
			for (int i=16;i<c1.length;i++) {
				if (c1[i] != c[i]) return use0[0] == false;
			}
			return use0[0] == true;
		}
	}
}