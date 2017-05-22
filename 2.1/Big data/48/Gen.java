import java.util.*;

public class Gen {
	List<W> list;
	
	public Gen(int n, int l, int m) {
		list = new ArrayList<W>(n);
		Random r = new Random();
		for (int i=0;i<n;i++) {
			int[] a = new int[l+1];
			for (int j=0;j<l+1;j++) a[j] = r.nextInt(m);
			list.add(new W(a, m));
		}
	}
	
	public List<W> getList() {
		return list;
	}
	
	public static void main(String[] args) {
		Gen g = new Gen(100, 3, 11);
		StringBuilder b = new StringBuilder();
		boolean start = true;
		for (W w : g.getList()) {
			if (start) {
				start = false;
			} else {
				b.append("\n");
			}
			b.append(w.toString());
		}
		System.out.println(b.toString());
	}
}