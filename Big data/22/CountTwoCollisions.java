import java.util.*;
import java.io.*;

public class CountTwoCollisions {
	public static void saveRows(int[][] rows) {
		try {
			OutputStream out = new FileOutputStream("rows.txt");
			for (int[] row : rows) {
				String line = "(" + row[0] + "," + row[1] + "," + row[2] + ")\n";
				out.write(line.getBytes());
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveMap(Map<HotelDay, List<Integer>> map) {
		try {
			OutputStream out = new FileOutputStream("map.txt");
			for (HotelDay hotelDay : map.keySet()) {
				String line = hotelDay.toString() + map.get(hotelDay) + "\n";
				out.write(line.getBytes());
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveMeetings(Map<Integer, List<Integer>> meetings) {
		try {
			OutputStream out = new FileOutputStream("meetings.txt");
			for (Integer id : meetings.keySet()) {
				String line = id.toString() + meetings.get(id) + "\n";
				out.write(line.getBytes());
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveCount(Map<Pair, Integer> count) {
		try {
			OutputStream out = new FileOutputStream("count.txt");
			for (Pair pair : count.keySet()) {
				String line = pair.toString() + " - " + count.get(pair) + "\n";
				out.write(line.getBytes());
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveResult(Map<Pair, Integer> count) {
		try {
			OutputStream out = new FileOutputStream("result.txt");
			for (Pair pair : count.keySet()) {
				if (count.get(pair)>1) {
					String line = pair.toString() + " - " + count.get(pair) + "\n";
					out.write(line.getBytes());
				}
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		int[][] rows = FileReader.readFile(args[0]);
		saveRows(rows);
		Map<HotelDay, List<Integer>> map = RowsToHotelDaysMapper.map(rows);
		saveMap(map);
		Map<Integer, List<Integer>> meetings = HotelDaysToMeetingsMapper.map(map);
		saveMeetings(meetings);
		Map<Pair, Integer> count = MeetingsToCount.reduce(meetings);
		saveCount(count);
		
		saveResult(count);
	}
}