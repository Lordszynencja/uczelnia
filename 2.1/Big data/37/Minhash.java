import java.util.*;

public class Minhash {
	int[] a;
	int[] b;
	int h;
	
	public Minhash(int h) {
		a = new int[h];
		b = new int[h];
		this.h = h;
		Random r = new Random();
		for (int i=0;i<h;i++) {
			a[i] = r.nextInt();
			b[i] = r.nextInt();
		}
	}
	
	private int[] getHashes(Set<Kgram> kgrams) {
		int[] hash = new int[h];
		Arrays.fill(hash, Integer.MAX_VALUE);
		for (Kgram kgram : kgrams) {
			for (int i=0;i<h;i++) {
				int newHash = a[i]*kgram.hashCode()+b[i];
				if (newHash<hash[i]) hash[i] = newHash;
			}
		}
		return hash;
	}
	
	public double minhash(String f1, String f2, int k) {
		Set<Kgram> s1 = Kgram.toKgrams(Splitter.splitFile(f1), k);
		Set<Kgram> s2 = Kgram.toKgrams(Splitter.splitFile(f2), k);
		
		int[] hashes1 = getHashes(s1);
		int[] hashes2 = getHashes(s2);
		
		int equal = 0;
		for (int i=0;i<h;i++) {
			if (hashes1[i] == hashes2[i]) equal++;
		}
		
		return (double)equal/(double)h;
	}
	
	public void testFor(String f1, String f2, int k) {
		System.out.println("##################");
		System.out.println("file1: " + f1);
		System.out.println("file2: " + f2);
		System.out.println("k: " + k);
		System.out.println("result: " + this.minhash(f1, f2, k));
		System.out.println("##################");
	}
	
	public static void main(String[] args) {
		int[] hashes = new int[]{50, 150, 250};
		for (int h : hashes) {
			System.out.println("\n\n################");
			System.out.println("FOR h="+h);
			Minhash minhash = new Minhash(h);
			minhash.testFor("Kgram.java", "Minhash.java", 4);
			minhash.testFor("RW.java", "Minhash.java", 4);
			minhash.testFor("Kgram.java", "RW.java", 4);
			
			minhash.testFor("kolor_magii0.txt", "kolor_magii0.txt", 7);
			minhash.testFor("kolor_magii0.txt", "kolor_magii1.txt", 7);
			minhash.testFor("kolor_magii1.txt", "kolor_magii2.txt", 7);
			minhash.testFor("kolor_magii2.txt", "kolor_magii3.txt", 7);
			minhash.testFor("kolor_magii3.txt", "kolor_magii4.txt", 7);
			
			minhash.testFor("kolor_magii3.txt", "kolor_magii4.txt", 3);
			
			minhash.testFor("test1.txt", "test2.txt", 2);
		}
	}
}