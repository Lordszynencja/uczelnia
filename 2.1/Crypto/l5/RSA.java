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
			BigInteger piMinusOne = p[i].subtract(BigInteger.ONE);
			phi = phi.multiply(piMinusOne).divide(phi.gcd(piMinusOne));
		}
		e = new BigInteger("3");
		d = e.modInverse(phi);
		System.out.println("d=" + d.toString());
		
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
	
	private byte[] encryptBlock(BigInteger block) {
		return block.modPow(e, n).toByteArray()
	}
	
	public byte[] encrypt(byte[] m, EncryptionModeEnum mode) {
		if (mode == EncryptionModeEnum.OFB || mode == EncryptionModeEnum.CBC) return null;
		if (!canEncrypt) return null;
		BigInteger m0 = new BigInteger(m);
		List<BigInteger> blocks = new LinkedList<BigInteger>();
		while (m0.compareTo(BigInteger.ZERO) == 1) {
			BigInteger[] mods = m0.divideWithRemainder(n);
			blocks.add(mods[1]);
			m0 = mods[0];
		}
		
		
		return m0.modPow(e, n).toByteArray();
	}
	
	public byte[] decrypt(byte[] c, EncryptionModeEnum mode, ) {
		if (mode == EncryptionModeEnum.OFB || mode == EncryptionModeEnum.CBC) return null;
		if (!canDecrypt) return null;
		BigInteger c0 = new BigInteger(c);
		if (c0.compareTo(n) >= 0) return null;
		
		return c0.modPow(d, n).toByteArray();
	}
}