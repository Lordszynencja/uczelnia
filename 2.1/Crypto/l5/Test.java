import java.math.*;
import java.util.*;

public class Test {
	public static void testRabin() {
		System.out.println("Rabin TEST STARTED");

		Date d0 = new Date();
		ICoder r = new Rabin();
		r.createKey();
		System.out.println("key created");
		Date d1 = new Date();
		ICoder r1 = new Rabin();
		r1.setPublicKey(r.getPrivateKey());
		ICoder r2 = new Rabin();
		r2.setPrivateKey(r.getPrivateKey());

		Date d2 = new Date();
		byte[] msg = "random msg".getBytes();
		byte[] c1 = r2.encrypt(msg, EncryptionModeEnum.STANDARD);
		byte[] c2 = r2.encrypt(msg, EncryptionModeEnum.CTR);
		Date d3 = new Date();
		byte[] m1 = r1.decrypt(c1, EncryptionModeEnum.STANDARD);
		byte[] m2 = r1.decrypt(c2, EncryptionModeEnum.CTR);
		Date d4 = new Date();
		if (!Arrays.equals(msg, m1) || !Arrays.equals(msg, m2)) System.out.println("NOT OK");
		
		System.out.println("key creation time: "+(d1.getTime()-d0.getTime())+"ms");
		System.out.println("encryption time: "+(d3.getTime()-d2.getTime())+"ms");
		System.out.println("decryption time: "+(d4.getTime()-d3.getTime())+"ms");
		System.out.println("\nRabin TEST FINISHED");
		System.out.println();
	}
  
	public static void testRSA() {
		System.out.println("\nRSA TEST STARTED");
		
		Date d0 = new Date();
		ICoder rsa = new RSA();
		rsa.createKey();
		System.out.println("key created");
		Date d1 = new Date();
		ICoder rsa1 = new RSA();
		rsa1.setPrivateKey(rsa.getPrivateKey());
		ICoder rsa2 = new RSA();
		rsa2.setPublicKey(rsa.getPublicKey());
		
		Date d2 = new Date();
		byte[] msg = "random msg".getBytes();
		byte[] c1 = rsa2.encrypt(msg, EncryptionModeEnum.STANDARD);
		byte[] c2 = rsa2.encrypt(msg, EncryptionModeEnum.CTR);
		Date d3 = new Date();
		byte[] m1 = rsa1.decrypt(c1, EncryptionModeEnum.STANDARD);
		byte[] m2 = rsa1.decrypt(c2, EncryptionModeEnum.CTR);
		Date d4 = new Date();
		if (!Arrays.equals(msg, m1) || !Arrays.equals(msg, m2)) System.out.println("NOT OK");
		
		System.out.println("key creation time: "+(d1.getTime()-d0.getTime())+"ms");
		System.out.println("encryption time: "+(d3.getTime()-d2.getTime())+"ms");
		System.out.println("decryption time: "+(d4.getTime()-d3.getTime())+"ms");
		System.out.println("\nRSA TEST FINISHED");
		System.out.println();
	}
	
	public static void testRSACRT() {
		System.out.println("\nRSACRT TEST STARTED");
		Date d0 = new Date();
		ICoder rsa = new RSACRT();
		rsa.createKey();
		System.out.println("key created");
		Date d1 = new Date();
		ICoder rsa1 = new RSACRT();
		rsa1.setPrivateKey(rsa.getPrivateKey());
		ICoder rsa2 = new RSACRT();
		rsa2.setPublicKey(rsa.getPublicKey());
		
		Date d2 = new Date();
		byte[] msg = "random msg".getBytes();
		byte[] c1 = rsa2.encrypt(msg, EncryptionModeEnum.STANDARD);
		byte[] c2 = rsa2.encrypt(msg, EncryptionModeEnum.CTR);
		Date d3 = new Date();
		byte[] m1 = rsa1.decrypt(c1, EncryptionModeEnum.STANDARD);
		byte[] m2 = rsa1.decrypt(c2, EncryptionModeEnum.CTR);
		Date d4 = new Date();
		if (!Arrays.equals(msg, m1) || !Arrays.equals(msg, m2)) System.out.println("NOT OK");
		
		System.out.println("key creation time: "+(d1.getTime()-d0.getTime())+"ms");
		System.out.println("encryption time: "+(d3.getTime()-d2.getTime())+"ms");
		System.out.println("decryption time: "+(d4.getTime()-d3.getTime())+"ms");
		System.out.println("\nRSACRT TEST FINISHED");
		System.out.println();
	}
	
	public static void testCores() {
		final int cores = Runtime.getRuntime().availableProcessors();
		System.out.println(cores);
		
		final int k = 15;
		
		BigInteger[] x = new BigInteger[k];
		Thread[] threads = new Thread[cores];
		for (int i=0;i<cores;i++) {
			final int threadId = i;
			threads[i] = new Thread() {
				public void run() {
					int id = threadId;
					int a = id;
					while (a<k) {
						System.out.println(a);
						x[a] = BigInteger.valueOf(id);
						a += cores;
					}
				}
			};
			threads[i].start();
		}
		boolean hasNull = true;
		int check = 0;
		while (hasNull) {
			hasNull = false;
			while (check < k && x[check] != null) {
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
	}
	
	public static void main(String[] args) {
		testRabin();
		testRSA();
		testRSACRT();
	}
}