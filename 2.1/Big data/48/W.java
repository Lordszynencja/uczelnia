import java.util.*;

public class W {
	int[] a;
	int m;
	
	public W(int[] a, int m) {
		this.a = new int[a.length];
		for (int i=0;i<a.length;i++) this.a[i] = a[i]%m;
		this.m = m;
	}
	
	public int value(int x) {
		int x0 = x%m;
		int value = 0;
		for (int i=0;i<a.length;i++) {
			value = (value*x0+a[i])%m;
		}
		return value;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i=0;i<a.length;i++) {
			if (i != 0) b.append("+");
			b.append(a[i]).append("x^").append(a.length-i-1);
		}
		return b.toString();
	}
}