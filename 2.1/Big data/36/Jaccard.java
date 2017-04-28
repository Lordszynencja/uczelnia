import java.util.*;

public class Jaccard {
	public static double jaccard(String f1, String f2, int k) {
		Set<Kgram> s1 = Kgram.toKgrams(FilesLoader.readFile(f1), k);
		Set<Kgram> s2 = Kgram.toKgrams(FilesLoader.readFile(f2), k);
		
		Set<Kgram> sum = new HashSet<Kgram>();
		Set<Kgram> inter = new HashSet<Kgram>();
		
		sum.addAll(s1);
		sum.addAll(s2);
		
		for (Kgram kgram : s1) {
			if (s2.contains(kgram)) inter.add(kgram);
		}
		
		if (inter.size()<10) System.out.println("inter: "+inter.toString());
		
		return (double)inter.size()/(double)sum.size();
	}
	
	public static void testFor(String f1, String f2, int k) {
		System.out.println("##################");
		System.out.println("file1: "+f1);
		System.out.println("file2: "+f2);
		System.out.println("k: "+k);
		System.out.println("result: "+Jaccard.jaccard(f1, f2, k));
		System.out.println("##################");
	}
	
	public static void main(String[] args) {
		Jaccard.testFor("Kgram.java", "Jaccard.java", 4);
		Jaccard.testFor("FilesLoader.java", "Jaccard.java", 4);
		Jaccard.testFor("Kgram.java", "FilesLoader.java", 4);
		
		Jaccard.testFor("kolor_magii0.txt", "kolor_magii1.txt", 7);
		Jaccard.testFor("kolor_magii1.txt", "kolor_magii2.txt", 7);
		Jaccard.testFor("kolor_magii2.txt", "kolor_magii3.txt", 7);
		Jaccard.testFor("kolor_magii3.txt", "kolor_magii4.txt", 7);
		
		Jaccard.testFor("kolor_magii3.txt", "kolor_magii4.txt", 3);
	}
}