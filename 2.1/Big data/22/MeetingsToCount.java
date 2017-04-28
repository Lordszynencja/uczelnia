import java.util.*;

public class MeetingsToCount {
	public static Map<Pair, Integer> reduce(final Map<Integer, List<Integer>> meetings) {
		final Map<Pair, Integer> reduced = new HashMap<Pair, Integer>();
		for (final Integer key : meetings.keySet()) {
			final List<Integer> ids = meetings.get(key);
	  
			for (Integer i : ids) {
				if (!i.equals(key)) {
					Pair p = (i>key ? new Pair(key, i) : new Pair(i, key));
					Integer count = reduced.get(p);
					if (count == null) count = 0;
					count += 1;
					reduced.put(p, count);
				}
			}
		}
		for (Pair i : reduced.keySet()) {
			reduced.put(i, reduced.get(i)/2);
		}
		return reduced;
	}
}
