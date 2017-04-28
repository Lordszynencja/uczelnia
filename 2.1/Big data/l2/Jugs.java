import java.util.*;

public class Jugs {
	private Jugs previous;
	private int[] jugs = new int[3];
	private static int[] jugsMax = {8, 5, 3};
	
	public Jugs() {
		this.previous = null;
		this.jugs[0] = 8;
		this.jugs[1] = 0;
		this.jugs[2] = 0;
	}
	
	public Jugs(Jugs previous) {
		this.previous = previous;
		this.jugs[0] = previous.jugs[0];
		this.jugs[1] = previous.jugs[1];
		this.jugs[2] = previous.jugs[2];
	}
	
	public List<Jugs> calculatePossibilities(Map<String, Jugs> existing) {
		List<Jugs> newPossibilities = new ArrayList<Jugs>();
		for (int i=0;i<3;i++) {
			for (int j=0;j<3;j++) {
				Jugs possibility = new Jugs(this);
				if (i!=j && possibility.move(i, j) && !existing.containsKey(possibility.toString())) {
					existing.put(possibility.toString(), possibility);
					newPossibilities.add(possibility);
				}
			}
		}
		return newPossibilities;
	}
	
	public boolean isFinal() {
		return this.jugs[0] == 4 && this.jugs[1] == 4;
	}
	
	private boolean move(int from, int to) {
		int sum = this.jugs[from] + this.jugs[to];
		if (sum<Jugs.jugsMax[to]) {
			this.jugs[from] = 0;
			this.jugs[to] = sum;
			return true;
		} else if (this.jugs[to] == Jugs.jugsMax[to]) {
			return false;
		} else {
			this.jugs[from] = sum-Jugs.jugsMax[to];
			this.jugs[to] = Jugs.jugsMax[to];
			return true;
		}
	}
	
	public List<Jugs> getHistory() {
		if (this.previous == null) {
			List<Jugs> history = new ArrayList<Jugs>();
			history.add(this);
			return history;
		} else {
			List<Jugs> history = this.previous.getHistory();
			history.add(this);
			return history;
		}
	}
	
	@Override
	public String toString() {
		return "[" + jugs[0] + "," + jugs[1] + "," + jugs[2] + "]"; 
	}
}