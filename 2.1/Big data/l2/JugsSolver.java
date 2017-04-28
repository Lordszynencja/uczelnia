import java.util.*;

public class JugsSolver {
	public static void main(String[] args) {
		List<Jugs> possibilities = new ArrayList<Jugs>();
		Map<String, Jugs> existing = new HashMap<String, Jugs>();
		Jugs start = new Jugs();
		possibilities.add(start);
		existing.put(start.toString(), start);
		while (!possibilities.isEmpty()) {
			Jugs actual = possibilities.remove(0);
			possibilities.addAll(actual.calculatePossibilities(existing));
		}
		for (String key : existing.keySet()) {
			Jugs jugs = existing.get(key);
			if (jugs.isFinal()) {
				List<Jugs> history = jugs.getHistory();
				for (Jugs hist : history) {
					System.out.print(hist.toString() + "->");
				}
				System.out.println("finish");
			}
		}
	}
}