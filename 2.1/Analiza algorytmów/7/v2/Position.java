import java.util.*;

public class Position {
	private static List<int[]> permutations;
	private static int permutationsSize = 1;
	public static Map<Integer, Integer> positions = new HashMap<Integer, Integer>();
	
	public static void preparePermutations(int n) {
		positions = new HashMap<Integer, Integer>();
		if (n>permutationsSize) {
			preparePermutations(n-1);
			List<int[]> oldPerms = permutations;
			permutations = new ArrayList<int[]>();
			for (int i=0;i<n;i++) {
				for (int[] p : oldPerms) {
					int[] perm = Arrays.copyOf(p, n);
					perm[n-1] = i;
					for (int j=0;j<n-1;j++) {
						if (perm[j] == i) {
							perm[j] = n-1;
							j = n;
						}
					}
					permutations.add(perm);
				}
			}
			permutationsSize = n;
		} else if (permutationsSize<2) {
			permutations = new ArrayList<int[]>();
			permutations.add(new int[] {0});
			permutationsSize = 1;
		}
	}
	
	public static void printPermutations() {
		for (int[] p : permutations) {
			System.out.print(p[0]);
			for (int i=1;i<p.length;i++) System.out.print(","+p[i]);
			System.out.println();
		}
	}
	
	public int[] r;
	public Position max = null;
	
	public Position(int[] r) {
		this.r = Arrays.copyOf(r, r.length);
	}
	
	public boolean isOk() {
		boolean ok = false;
		if (r[0] == r[r.length-1]) ok = true;
		for (int i=1;i<r.length;i++) {
			if (r[i] != r[i-1]) {
				if (ok) return false;
				else ok = true;
			}
		}
		return ok;
	}
	
	private boolean run(int i) {
		if (i == 0) {
			if (r[0] == r[r.length-1]) {
				r[0] = (r[0]+1)%r.length;
				return true;
			}
		} else {
			if (r[i] != r[i-1]) {
				r[i] = r[i-1];
				return true;
			}
		}
		return false;
	}
	
	public Integer calculate() {
		if (positions.get(this.hashCode()) != null) {
			return positions.get(this.hashCode());
		} else if (this.isOk()) {
			positions.put(this.hashCode(), new Integer(0));
			return new Integer(0);
		} else {
			Integer max = 0;
			for (int[] p : permutations) {
				Position newPos = new Position(r);
				int runs = 0;
				for (int i=0;i<r.length;i++) {
					if (newPos.run(p[i])) runs++;
				}
				Integer steps = positions.get(newPos.hashCode());
				if (steps == null) {
					steps = newPos.calculate()+runs;
				} else {
					steps = steps+runs;
				}
				if (steps>max) max = steps;
			}
			positions.put(this.hashCode(), max);
			return max;
		}
	}
	
	public int hashCode() {
		int h = 0;
		for (int i=0;i<r.length;i++) h = h*10+r[i];
		return h;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Position) || ((Position)o).r.length != this.r.length) return false;
		Position p = (Position)o;
		for (int i=0;i<r.length;i++) if (p.r[i] != r[i]) return false;
		return true;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append(r[0]);
		for (int i=1;i<r.length;i++) sb.append(",").append(r[i]);
		sb.append("]");
		return sb.toString();
	}
}