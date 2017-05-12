import java.util.*;

public class Counter {
	public static Random r = new Random();
	
	private int c = 0;
	private double chance = 1.0;
	
	public void increment() {
		if (r.nextDouble() < chance) {
			chance = chance/2.0;
			c++;
		}
	}
	
	public int getValue() {
		return c;
	}
	
	public static double calculateResults(int[] res) {
		double med = 0.0;
		for (int i=0;i<res.length;i++) {
			med += (double)res[i];
		}
		med /= (double) res.length;
		return Math.pow(2.0, med);
	}
	
	public static void main(String[] args) {
		int countersNo = 4;
		Counter[] counters = new Counter[countersNo];
		int n = 10000;
		int tries = 100;
		int[] results = new int[countersNo];
		int sum = 0;
		
		System.out.println("n = "+n);
		for (int i=1;i<=tries;i++) {
			for (int j=0;j<countersNo;j++) {
				counters[j] = new Counter();
			}
			for (int j=0;j<countersNo;j++) {
				for (int k=0;k<n;k++) {
					counters[j].increment();
				}
				results[j] = counters[j].getValue();
			}
			int result = (int)calculateResults(results);
			System.out.print(result+",");
			sum += result;
			if (i%5 == 0) System.out.println();
		}
		System.out.println("medium:"+(sum/tries));
	}
}