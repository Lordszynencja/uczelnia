import java.math.*;
import java.util.*;
import java.security.SecureRandom;

public class RSA implements ICoder {
	public static int defaultKeysize = 2048;
	public static int defaultK = 2;
	
	private boolean canEncrypt = false;
	private boolean canDecrypt = false;
	private BigInteger n;
	private BigInteger e;
	private BigInteger d;
	
	public void createKey() {
		createKey(defaultKeysize);
	}
	
	public void createKey(int keysize) {
		createKey(defaultK, keysize);
	}
	
	public void createKey(int k, int keysize) {
		Random r = new SecureRandom();
		BigInteger[] p = new BigInteger[k];
		n = BigInteger.ONE;
		BigInteger phi = BigInteger.ONE;
		for (int i=0;i<k;i++) {
			p[i] = new BigInteger(keysize, 40, r);
			n = n.multiply(p[i]);
			phi = phi.multiply(p[i].subtract(BigInteger.ONE));
		}
		e = new BigInteger("3");
		d = e.modInverse(phi);
		
		canEncrypt = true;
		canDecrypt = true;
	}
	
	public byte[] getPublicKey() {
		List<byte[]> list = new LinkedList<byte[]>();
		list.add(n.toByteArray());
		list.add(e.toByteArray());
		return Transformer.join(list);
	}
	
	public void setPublicKey(byte[] k) {
		List<byte[]> list = Transformer.split(k);
		n = new BigInteger(list.get(0));
		e = new BigInteger(list.get(1));
		canEncrypt = true;
	}
	
	public byte[] getPrivateKey() {
		List<byte[]> list = new LinkedList<byte[]>();
		list.add(n.toByteArray());
		list.add(d.toByteArray());
		return Transformer.join(list);
	}
	
	public void setPrivateKey(byte[] k) {
		List<byte[]> list = Transformer.split(k);
		n = new BigInteger(list.get(0));
		d = new BigInteger(list.get(1));
		canEncrypt = true;
	}
	
	public byte[] encrypt(byte[] m, EncryptionModeEnum mode) {
		if (mode == OFB || mode == CBC) return null;
		if (!canEncrypt) return null;
		BigInteger m0 = new BigInteger(m);
		if (m0.compareTo(n) >= 0) return null;
		
		return m0.modPow(e, n).toByteArray();
	}
	
	public byte[] decrypt(byte[] c, EncryptionModeEnum mode) {
		if (mode == OFB || mode == CBC) return null;
		if (!canDecrypt) return null;
		BigInteger c0 = new BigInteger(c);
		if (c0.compareTo(n) >= 0) return null;
		
		return c0.modPow(d, n).toByteArray();
	}
}