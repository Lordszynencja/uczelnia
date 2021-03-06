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
	private Random r = new SecureRandom();
	
	public void createKey() {
		createKey(defaultKeysize);
	}
	
	public void createKey(int keysize) {
		createKey(defaultK, keysize);
	}
	
	public void createKey(int k, int keysize) {
		BigInteger[] p = new BigInteger[k];
		n = BigInteger.ONE;
		e = new BigInteger("17");
		boolean isOk = false;
		while (!isOk) {
			try {
				BigInteger phi = BigInteger.ONE;
				for (int i=0;i<k;i++) {
					p[i] = new BigInteger(keysize, 40, r);
					n = n.multiply(p[i]);
					BigInteger piMinusOne = p[i].subtract(BigInteger.ONE);
					phi = phi.multiply(piMinusOne).divide(phi.gcd(piMinusOne));
				}
				d = e.modInverse(phi);
				isOk = true;
			} catch(ArithmeticException e) {
			}
		}
		
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
		canDecrypt = true;
	}
	
	private byte[] encryptBlock(BigInteger block) {
		return block.modPow(e, n).toByteArray();
	}
	
	public byte[] encrypt(byte[] m, EncryptionModeEnum mode) {
		if (mode == EncryptionModeEnum.OFB || mode == EncryptionModeEnum.CBC) return null;
		if (!canEncrypt) return null;
		BigInteger m0 = new BigInteger(m);
		BigInteger iv = new BigInteger(128, r);
		List<byte[]> blocks = new LinkedList<byte[]>();
		blocks.add(iv.toByteArray());
		while (m0.compareTo(BigInteger.ZERO) == 1) {
			BigInteger[] mods = m0.divideAndRemainder(n);
			if (mode == EncryptionModeEnum.CTR) blocks.add(encryptBlock(mods[1].add(iv).mod(n)));
			else blocks.add(encryptBlock(mods[1]));
			m0 = mods[0];
			iv = iv.add(BigInteger.ONE);
		}
		
		return Transformer.join(blocks);
	}
	
	private BigInteger decryptBlock(byte[] block) {
		return new BigInteger(block).modPow(d, n);
	}
	
	public byte[] decrypt(byte[] c, EncryptionModeEnum mode) {
		if (mode == EncryptionModeEnum.OFB || mode == EncryptionModeEnum.CBC) return null;
		if (!canDecrypt) return null;
		List<byte[]> blocks = Transformer.split(c);
		BigInteger iv = new BigInteger(blocks.get(0));
		BigInteger m = BigInteger.ZERO;
		for (int i=blocks.size()-1;i>0;i--) {
			if (mode == EncryptionModeEnum.CTR) m = m.multiply(n).add(decryptBlock(blocks.get(i)).subtract(iv.add(BigInteger.valueOf(i-1))).mod(n));
			else m = m.multiply(n).add(decryptBlock(blocks.get(i)));
		}
		
		return m.toByteArray();
	}
}