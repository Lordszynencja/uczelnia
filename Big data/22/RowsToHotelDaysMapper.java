import java.util.*;

public class RowsToHotelDaysMapper {
	public static Map<HotelDay, List<Integer>> map(final int[][] rows) {
		final Map<HotelDay, List<Integer>> map = new HashMap<HotelDay, List<Integer>>();
		for (final int[] row : rows) {
			final HotelDay hotelDay = new HotelDay(row[0], row[1]);
			List<Integer> persons = map.get(hotelDay);
			if (persons == null) {
				persons = new ArrayList<Integer>();
			}
			persons.add(row[2]);
			map.put(hotelDay, persons);
		}
		return map;
	}
}
