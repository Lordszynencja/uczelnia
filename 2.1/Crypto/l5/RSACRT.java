import java.math.*;
import java.util.*;
import java.security.SecureRandom;

public class RSACRT implements ICoder {
	public static int defaultKeysize = 1024;
	public static int defaultK = 4;
	
	private boolean canEncrypt = false;
	private boolean canDecrypt = false;
	private int k;
	private BigInteger n;
	private BigInteger e;
	private BigInteger p[];
	private BigInteger d[];
	private BigInteger t[];
	private Random r = new SecureRandom();
	
	public void createKey() {
		createKey(defaultKeysize);
	}
	
	public void createKey(int keysize) {
		createKey(defaultK, keysize);
	}
	
	public void createKey(int k, int keysize) {
		this.k = k;
		n = BigInteger.ONE;
		e = new BigInteger("17");
		boolean isOk = false;
		while (!isOk) {
			try {
				p = new BigInteger[k];
				d = new BigInteger[k];
				t = new BigInteger[k-1];
				for (int i=0;i<k;i++) {
					p[i] = new BigInteger(keysize, 40, r);
					n = n.multiply(p[i]);
					d[i] = e.modInverse(p[i].subtract(BigInteger.ONE));
				}
				BigInteger m = p[0];
				for (int i=1;i<k;i++) {
					t[i-1] = m.modInverse(p[i]);
					m = m.multiply(p[i]);
				}
				isOk = true;
			} catch (ArithmeticException e) {
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
		for (int i=0;i<k;i++) {
			list.add(p[i].toByteArray());
		}
		for (int i=0;i<k;i++) {
			list.add(d[i].toByteArray());
		}
		for (int i=0;i<k-1;i++) {
			list.add(t[i].toByteArray());
		}
		return Transformer.join(list);
	}
	
	public void setPrivateKey(byte[] key) {
		List<byte[]> list = Transformer.split(key);
		k = list.size()/3;
		n = new BigInteger(list.get(0));
		p = new BigInteger[k];
		for (int i=0;i<k;i++) {
			p[i] = new BigInteger(list.get(i+1));
		}
		d = new BigInteger[k];
		for (int i=0;i<k;i++) {
			d[i] = new BigInteger(list.get(i+k+1));
		}
		t = new BigInteger[k-1];
		for (int i=0;i<k-1;i++) {
			t[i] = new BigInteger(list.get(i+k+k+1));
		}
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
		BigInteger c = new BigInteger(block);
		int cores = Runtime.getRuntime().availableProcessors();
		final int coresNo = (cores > k ? k : cores);
		
		final BigInteger[] mi = new BigInteger[k];
		Thread[] threads = new Thread[coresNo];
		for (int i=0;i<coresNo;i++) {
			final int threadId = i;
			threads[i] = new Thread() {
				public void run() {
					int a = threadId;
					while (a<k) {
						mi[a] = c.modPow(d[a], p[a]);
						a += coresNo;
					}
				}
			};
			threads[i].start();
		}
		
		boolean hasNull = true;
		int check = 0;
		while (hasNull) {
			hasNull = false;
			while (check < k && mi[check] != null) {
				check++;
			}
			if (check < k) {
				hasNull = true;
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					
				}
			}
		}
		
		BigInteger x = mi[0];
		BigInteger m = p[0];
		for (int i=1;i<k;i++) {
			x = x.add(m.multiply(mi[i].subtract(x).multiply(t[i-1]).mod(p[i])));
			m = m.multiply(p[i]);
		}
		
		return x;
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