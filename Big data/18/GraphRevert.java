import java.util.*;
import java.io.*;

public class GraphRevert {
	public static Map<Integer, List<Integer>> getGraphFromFile(String filename) {
		Map<Integer, List<Integer>> graph = new HashMap<Integer, List<Integer>>();
		String file = "";
		try {
			File f = new File(filename);
			InputStream input = new FileInputStream(f);
			byte[] bytes = new byte[(int)f.length()];
			input.read(bytes);
			file = new String(bytes, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int state = 0;
		String val = "";
		Integer key = null;
		List<Integer> values = null;
		
		for (char c : file.toCharArray()) {
			if (state == 0 && c >= '0' && c <= '9') {
				val += c;
			} else if (state == 0 && c == ',') {
				state = 1;
				key = Integer.valueOf(val);
				val = "";
				values = new ArrayList<Integer>();
			} else if (state == 1 && c >= '0' && c <= '9') {
				val += c;
			} else if (state == 1 && c == ',') {
				values.add(Integer.valueOf(val));
				val = "";
			} else if (state == 1 && c == ']') {
				values.add(Integer.valueOf(val));
				val = "";
				graph.put(key, values);
				state = 0;
			}
		}
		return graph;
	}
	
	public static void printGraph(Map<Integer, List<Integer>> graph) {
		for (Integer key : graph.keySet()) {
			StringBuilder s = new StringBuilder();
			s.append(key.toString()).append("->");
			for (Integer i : graph.get(key)) {
				s.append(i.toString()).append(",");
			}
			System.out.println(s.toString());
		}
	}
	
	public static Map<Integer, List<Integer>> map(Map<Integer, List<Integer>> graph) {
		Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		
		for (Integer key : graph.keySet()) {
			for (Integer id : graph.get(key)) {
				List<Integer> ids = map.get(id);
				if (ids == null) ids = new ArrayList<Integer>();
				ids.add(key);
				map.put(id, ids);
			}
		}
		
		return map;
	}
	
	public static Map<Integer, List<Integer>> reduce(Map<Integer, List<Integer>> map) {
		Map<Integer, List<Integer>> reduced = new HashMap<Integer, List<Integer>>();
		for (Integer key : map.keySet()) {
			List<Integer> ids = map.get(key);
			List<Integer> newIds = new ArrayList<Integer>();
			for (Integer id : ids) newIds.add(id);
			reduced.put(key, newIds);
		}
		return reduced;
	}
	
	public static void main(String[] args) {
		Map<Integer, List<Integer>> graph = GraphRevert.getGraphFromFile(args[0]);
		Map<Integer, List<Integer>> map = GraphRevert.map(graph);
		Map<Integer, List<Integer>> reduced = GraphRevert.reduce(map);
		GraphRevert.printGraph(reduced);
	}
}