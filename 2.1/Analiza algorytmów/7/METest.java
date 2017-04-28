import java.util.*;
import java.text.*;

public class METest {
	private static int test(int[] r, int n, int pos) {
		if (pos == 0) {
			int max = 0;
			for (int i=0;i<n;i++) {
				r[0] = i;
				Position p = new Position(r);
				int t = p.calculate();
				if (t>max) max = t;
			}
			return max;
		} else if (pos == n-1) {
			int max = 0;
			for (int i=0;i<n;i++) {
				System.out.print(i + ":");
				r[pos] = i;
				int t = test(r, n, pos-1);
				if (t>max) max = t;
			}
			return max;
		} else if (pos == n-2) {
			int max = 0;
			for (int i=0;i<n;i++) {
				System.out.print(i+",");
				r[pos] = i;
				int t = test(r, n, pos-1);
				if (t>max) max = t;
			}
			System.out.println();
			return max;
		} else {
			int max = 0;
			for (int i=0;i<n;i++) {
				r[pos] = i;
				int t = test(r, n, pos-1);
				if (t>max) max = t;
			}
			return max;
		}
	}
	
	private static int test(int n) {
		if (n <= 0) return 0;
		return test(new int[n], n, n-1);
	}
	
	public static void main(String[] args) {
		NumberFormat format = new DecimalFormat("000");
		StringBuilder sb = new StringBuilder();
		for (int i=1;i<=8;i++) {
			System.out.println("n : "+i);
			Date d0 = new Date();
			sb.append("n=").append(i).append(" : ");
			RW.write("results.txt", sb.toString());
			Position.preparePermutations(i);
			Date d1 = new Date();
			long d01 = d1.getTime()-d0.getTime();
			System.out.println("permutations: " + d01/1000 + "." + format.format((int)d01%1000));
			sb.append(test(i)).append("\n");
			RW.write("results.txt", sb.toString());
			Date d2 = new Date();
			long d12 = d2.getTime()-d1.getTime();
			System.out.println("calculations: " + d12/1000 + "." + format.format((int)d12%1000));
		}
	}
}