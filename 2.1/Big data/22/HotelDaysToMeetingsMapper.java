import java.util.*;

public class HotelDaysToMeetingsMapper {
	public static Map<Integer, List<Integer>> map(final Map<HotelDay, List<Integer>> map1) {
		final Map<Integer, List<Integer>> meetings = new HashMap<Integer, List<Integer>>();
		for (final HotelDay key : map1.keySet()) {
			final List<Integer> ids = map1.get(key);
			final List<Integer> usedIds = new ArrayList<Integer>();
			for (final Integer id : ids) {
				if (!usedIds.contains(id)) {
					usedIds.add(id);
				}
			}
			
			for (final Integer id : usedIds) {
				List<Integer> met = meetings.get(id);
				if (met == null) {
					met = new ArrayList<Integer>();
				}
				for (final Integer id2 : usedIds) {
					met.add(id2);
				}
				meetings.put(id, met);
			}
		}
		return meetings;
	}
}
