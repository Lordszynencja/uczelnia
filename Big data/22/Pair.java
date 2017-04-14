public class Pair {
	int id1;
	int id2;

	public Pair(final int id1, final int id2) {
		this.id1 = id1;
		this.id2 = id2;
	}
	
	public boolean equals(final Pair pair) {
		return (this.id1 == pair.id1) && (this.id2 == pair.id2);
	}

	public boolean equals(final Object o) {
		if (o instanceof Pair) return this.equals((Pair)o);
		else return false;
	}
  
	public String toString() {
		return "(" + id1 + "-" + id2 + ")";
	}
	
	public int hashCode() {
		return this.id1*10000+this.id2;
	}
}
