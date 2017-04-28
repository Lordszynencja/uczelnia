import java.util.*;

public class Signaturer {
	public static int dot(double[] v, double[] w) {
		double dot = 0.0;
		for (int i=0;i<v.length;i++) {
			dot += v[i]*w[i];
		}
		return (dot>=0 ? 1 : 0);
	}
	
	private static int signatureSize = 1024;
	private double[][] vectors;
	private String[] words;
	
	public Signaturer(String[] words) {
		this.words = words;
		this.vectors = new double[signatureSize][words.length];
		Random r = new Random();
		for (int i=0;i<signatureSize;i++) {
			for (int j=0;j<words.length;j++) {
				vectors[i][j] = r.nextDouble()*2-1.0;
			}
		}
	}
	
	public Signature sign(String filename) {
		Map<String, Integer> count = WordCounter.countWords(Splitter.splitFile(filename));
		double[] wordVector = new double[words.length];
		for (int i=0;i<words.length;i++) {
			Integer wordCount = count.get(words[i]);
			wordVector[i] = (wordCount == null ? 0 : Double.valueOf(wordCount));
		}
		
		Signature s = new Signature(signatureSize);
		for (int i=0;i<signatureSize;i++) {
			s.setBit(i, dot(vectors[i], wordVector));
		}
		return s;
	}
	
	public static double similarity(Signature s1, Signature s2) {
		return 1.0-(double)s1.hammingDistance(s2)/(double)signatureSize;
	}
	
	public static void main(String[] args) {
		if (args.length > 2) {
			Signaturer s = new Signaturer(Splitter.splitFile(args[0]));
			Signature[] signatures = new Signature[args.length-1];
			for (int i=0;i<args.length-1;i++) {
				signatures[i] = s.sign(args[i+1]);
			}
			for (int i=0;i<signatures.length-1;i++) {
				for (int j=i+1;j<signatures.length;j++) {
					System.out.println("'"+args[i+1]+"' -> '"+args[j+1]+"' : "+similarity(signatures[i], signatures[j]));
				}
			}
		} else {
			System.err.println("usage:");
			System.err.println("java Signaturer <wordFile> <file0> <file1> ...");
		}
	}
}