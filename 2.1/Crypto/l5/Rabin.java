import java.math.*;
import java.security.*;
import java.util.*;

public class Rabin implements ICoder{
	private static int defaultKeySize = 2048;

	private boolean canEncrypt = false;
	private boolean canDecrypt = false;
	private BigInteger n;
	private BigInteger p;
	private BigInteger q;
	private BigInteger yp;
	private BigInteger yq;
	private Random r = new SecureRandom();
	
	public void createKey() {
		createKey(defaultKeySize);
	}
	
	public void createKey(int keysize) {
		p = new BigInteger(keysize, 100, r);
		while (!p.mod(new BigInteger("4")).equals(new BigInteger("1"))) {
			p = new BigInteger(keysize, 100, r);
		}
		q = new BigInteger(keysize, 100, r);
		while (!q.mod(new BigInteger("4")).equals(new BigInteger("1"))) {
			q = new BigInteger(keysize, 100, r);
		}
		final BigInteger[] ypq = BigIntAlg.extendedGCD(p, q);
		yp = ypq[0];
		yq = ypq[1];
		canDecrypt = true;

		n = p.multiply(q);
		canEncrypt = true;
	}
	
	public void createKey(int k, int keysize) {
		createKey(keysize);
	}

	public byte[] getPublicKey() {
		return getPrivateKey();
	}

	public void setPublicKey(byte[] k) {
		setPrivateKey(k);
	}

	public byte[] getPrivateKey() {
		List<byte[]> key = new ArrayList<byte[]>(2);
		key.add(p.toByteArray());
		key.add(q.toByteArray());

		return Transformer.join(key);
	}

	public void setPrivateKey(byte[] k) {
		List<byte[]> key = Transformer.split(k);
		p = new BigInteger(key.get(0));
		q = new BigInteger(key.get(1));
		key.add(q.toByteArray());
		
		BigInteger[] ypq = BigIntAlg.extendedGCD(p, q);
		yp = ypq[0];
		yq = ypq[1];

		n = p.multiply(q);
		canDecrypt = true;
		canEncrypt = true;
	}

	private BigInteger[] getPossibleValues(BigInteger block) {
		Thread[] threads = new Thread[2];
		BigInteger[] values = new BigInteger[4];
		final BigInteger[] tmp = new BigInteger[2];
		threads[0] = new Thread() {
			public void run() {
				tmp[0] = yp.multiply(p).multiply(BigIntAlg.modSqrt(block.mod(q), q));
				while (tmp[1] == null) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {}
				}
				values[0] = tmp[0].add(tmp[1]).mod(n);
				values[1] = n.subtract(values[0]);
			}
		};
		threads[1] = new Thread() {
			public void run() {
				tmp[1] = yq.multiply(q).multiply(BigIntAlg.modSqrt(block.mod(p), p));
				while (tmp[0] == null) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {}
				}
				values[2] = tmp[0].subtract(tmp[1]).mod(n);
				values[3] = n.subtract(values[2]);
			}
		};
		threads[0].start();
		threads[1].start();
		
		int check = 0;
		while (check < 4) {
			if (values[check] != null) check++;
			else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			}
		}
		return values;
	}

	private BigInteger encryptBigInt(BigInteger i) {
		return i.multiply(i).mod(n);
	}

	public byte[] encrypt(final byte[] m, EncryptionModeEnum mode) {
		if (!canEncrypt || (mode != EncryptionModeEnum.STANDARD && mode != EncryptionModeEnum.CTR)) {
			return null;
		}
		List<byte[]> byteList = new LinkedList<byte[]>();
		BigInteger iv = new BigInteger(128, r);
		byteList.add(iv.toByteArray());
		BigInteger value = new BigInteger(m);
		while (!BigInteger.ZERO.equals(value)) {
			BigInteger[] divided = value.divideAndRemainder(n);
			value = divided[0];
			BigInteger toEncrypt = null;
			if (mode == EncryptionModeEnum.STANDARD) {
				toEncrypt = divided[1];
			} else if (mode == EncryptionModeEnum.CTR) {
				toEncrypt = divided[1].add(iv).mod(n);
				iv = iv.add(BigInteger.ONE);
			}
			BigInteger encrypted = encryptBigInt(toEncrypt);
			byteList.add(encrypted.toByteArray());
			BigInteger[] possibleValues = getPossibleValues(encrypted);
			for (int i=0;i<4;i++) {
				if (possibleValues[i].equals(toEncrypt)) {
					byteList.add(new byte[] {(byte)i});
					i = 4;
				}
			}
		}

		return Transformer.join(byteList);
	}

	private BigInteger decryptBlock(BigInteger block, int chosen) {
		return getPossibleValues(block)[chosen];
	}

	public byte[] decrypt(byte[] c, EncryptionModeEnum mode) {
		if (!canDecrypt || (mode != EncryptionModeEnum.STANDARD && mode != EncryptionModeEnum.CTR)) {
			return null;
		}
		List<byte[]> byteList = Transformer.split(c);
		BigInteger iv = new BigInteger(byteList.get(0));
		BigInteger m = BigInteger.ZERO;

		for (int i=byteList.size()-2;i>=1;i-=2) {
			BigInteger decrypted = decryptBlock(new BigInteger(byteList.get(i)), byteList.get(i + 1)[0]);
			if (mode == EncryptionModeEnum.CTR) {
				decrypted = decrypted.subtract(iv.add(BigInteger.valueOf(i/2))).mod(n);
			}
			m = m.multiply(n).add(decrypted);
		}

		return m.toByteArray();
	}
}
