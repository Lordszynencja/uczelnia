import java.util.*;
import java.math.*;

public class MerkleHellmanKnapsack implements ICoder {
	public static void test() {
		ICoder coder = new MerkleHellmanKnapsack();
		ICoder coder2 = new MerkleHellmanKnapsack();
		coder2.createKey();
		coder.setPublicKey(coder2.getPublicKey());
		
		byte[] m = new byte[100];
		new Random().nextBytes(m);
		byte[] c = coder.encrypt(m);
		byte[] d = coder2.decrypt(c);
		
		boolean testPassed = true;
		if (d.length != m.length) {
			testPassed = false;
		} else {
			for (int i=0;i<m.length;i++) {
				if (d[i] != m[i]) {
					testPassed = false;
					i = m.length;
				}
			}
		}
		if (testPassed) System.err.println("Coder test passed");
		else System.err.println("Coder test failed");
	}
	
	private static int keysize = 8;
	private static int numbersSize = 32;
	private boolean canEncrypt = false;
	private boolean canDecrypt = false;
	private BigInteger[] w = new BigInteger[keysize];
	private BigInteger q;
	private BigInteger r;
	private BigInteger s;
	private BigInteger[] b = new BigInteger[keysize];
	
	public void createKey() {
		Random rand = new Random();
		q = new BigInteger(numbersSize, rand);
		for (int i=0;i<keysize;i++) {
			w[i] = new BigInteger(numbersSize, rand);
			for (int j=0;j<i;j++) w[i] = w[i].add(w[j]);
			q = q.add(w[i]);
		}
		r = new BigInteger(numbersSize, 100, rand);
		s = r.modInverse(q);
		canDecrypt = true;
		
		calculatePublicKey();
	}
	
	private void calculatePublicKey() {
		for (int i=0;i<keysize;i++) {
			b[i] = w[i].multiply(r).mod(q);
		}
		canEncrypt = true;
	}
	
	public byte[] getPublicKey() {
		List<byte[]> publicKeyParts = new ArrayList<byte[]>(b.length);
		for (BigInteger i : b) publicKeyParts.add(i.toByteArray());
		return Transformer.join(publicKeyParts);
	}
	
	public void setPublicKey(byte[] k) {
		List<byte[]> publicKeyParts = Transformer.split(k);
		if (publicKeyParts.size() != keysize) {
			System.err.println("[ERROR] Wrong key size");
		} else {
			int i = 0;
			for (byte[] bytes : publicKeyParts) {
				b[i] = new BigInteger(bytes);
				i++;
			}
			canEncrypt = true;
		}
	}
	
	public byte[] getPrivateKey() {
		List<byte[]> privateKeyParts = new ArrayList<byte[]>(keysize+2);
		for (BigInteger i : w) privateKeyParts.add(i.toByteArray());
		privateKeyParts.add(q.toByteArray());
		privateKeyParts.add(r.toByteArray());
		return Transformer.join(privateKeyParts);
	}
	
	public void setPrivateKey(byte[] k) {
		List<byte[]> privateKeyParts = Transformer.split(k);
		for (int i=0;i<keysize;i++) {
			w[i] = new BigInteger(privateKeyParts.get(i));
		}
		q = new BigInteger(privateKeyParts.get(keysize));
		r = new BigInteger(privateKeyParts.get(keysize+1));
		s = r.modInverse(q);
		canDecrypt = true;
		
		
		calculatePublicKey();
	}
	
	private byte[] encryptBlock(byte[] block) {
		BigInteger c = BigInteger.ZERO;
		for (int i=0;i<keysize;i++) {
			if (((int)block[i/8] & (1<<(i%8))) > 0) c = c.add(b[i]);
		}
		return c.toByteArray();
	}
	
	public byte[] encrypt(byte[] m) {
		if (!canEncrypt) return null;
		byte[][] blocks = Transformer.splitToBlocks(m, keysize/8);
		byte[][] encrypted = new byte[blocks.length][];
		for (int i=0;i<blocks.length;i++) {
			encrypted[i] = encryptBlock(blocks[i]);
		}
		return Transformer.joinArray(encrypted);
	}
	
	private byte[] decryptBlock(byte[] block) {
		BigInteger c = new BigInteger(block).multiply(s).mod(q);
		byte[] m = new byte[keysize/8];
		for (int i=keysize-1;i>=0;i--) {
			if (c.compareTo(w[i]) != -1) {
				m[i/8] |= 1<<(i%8);
				c = c.subtract(w[i]);
			}
		}
		return m;
	}
	
	public byte[] decrypt(byte[] c) {
		if (!canDecrypt) return null;
		byte[][] encrypted = Transformer.splitToArray(c);
		byte[][] blocks = new byte[encrypted.length][];
		for (int i=0;i<blocks.length;i++) {
			blocks[i] = decryptBlock(encrypted[i]);
		}
		return Transformer.mergeBlocks(blocks, keysize/8);
	}
}